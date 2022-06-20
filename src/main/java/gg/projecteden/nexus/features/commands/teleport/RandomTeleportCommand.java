package gg.projecteden.nexus.features.commands.teleport;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Cooldown;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.lwc.LWCProtection;
import gg.projecteden.nexus.models.lwc.LWCProtectionService;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.SubWorldGroup;
import io.papermc.lib.PaperLib;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Aliases({"randomtp", "rtp", "wild"})
public class RandomTeleportCommand extends CustomCommand {
	private static final List<SubWorldGroup> ALLOWED_WORLD_GROUPS = List.of(SubWorldGroup.SURVIVAL, SubWorldGroup.RESOURCE);
	private static final LWCProtectionService service = new LWCProtectionService();
	private final AtomicInteger count = new AtomicInteger(0);
	private boolean running = false;

	public RandomTeleportCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Async
	@Cooldown(value = TickTime.SECOND, x = 30, bypass = Group.ADMIN)
	void rtp() {
		if (!ALLOWED_WORLD_GROUPS.contains(subWorldGroup()))
			return;

		if (world().getEnvironment() != Environment.NORMAL)
			error("You must be in a survival overworld to run this command");

		if (!running) {
			send(PREFIX + "Teleporting to random location");
			running = true;
		}

		count.getAndIncrement();

		int radius = 0;
		switch (world().getName()) {
			case "survival" -> radius = 5000;
			case "resource" -> radius = 2500;
			default -> error("Could not find world border of current world");
		}

		int range = 250;
		List<Location> locationList = LocationUtils.getRandomPointInCircle(world(), radius);

		locationList.sort(Comparator.comparingInt(loc -> (int) (getDensity(loc, range) * 100000)));
		Location best = locationList.get(0);
		if (Nexus.getEnv() == Env.PROD)
			if (service.getProtectionsInRange(best, 50).size() != 0 && count.get() < 5) {
				rtp();
				return;
			}

		PaperLib.getChunkAtAsync(best, true).thenAccept(chunk -> {
			Block highestBlock = world().getHighestBlockAt(best);
			if (!highestBlock.getType().isSolid() && count.get() < 10) {
				Tasks.async(this::rtp);
				return;
			}

			player().teleportAsync(LocationUtils.getCenteredLocation(highestBlock.getLocation().add(0, 1, 0)), TeleportCause.COMMAND);
		});
	}

	public static double getDensity(Location location, int range) {
		if (Nexus.getEnv() != Env.PROD)
			return 0;

		List<LWCProtection> protections = service.getProtectionsInRange(location, range);
		return (protections.size() / Math.pow(range * 2.0, 2.0)) * 100;
	}
}
