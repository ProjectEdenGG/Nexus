package gg.projecteden.nexus.features.commands.teleport;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Cooldown;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.lwc.LWCProtection;
import gg.projecteden.nexus.models.lwc.LWCProtectionService;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.nexus.utils.worldgroup.SubWorldGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Aliases({"randomtp", "rtp", "wild"})
public class RandomTeleportCommand extends CustomCommand {
	private static final LWCProtectionService service = new LWCProtectionService();
	private final AtomicInteger count = new AtomicInteger(0);
	private boolean running = false;

	public RandomTeleportCommand(CommandEvent event) {
		super(event);
	}

	@Getter
	@AllArgsConstructor
	private enum RTPWorld {
		SURVIVAL,
		RESOURCE,
		;

		public World getWorld() {
			return Objects.requireNonNull(Bukkit.getWorld(name().toLowerCase()));
		}
	}

	@Path("[world]")
	@Async
	@Cooldown(value = TickTime.SECOND, x = 30, bypass = Group.ADMIN)
	@Description("Teleport to a random location in the Survival or Resource overworld")
	void rtp(RTPWorld rtpWorld) {
		if (rtpWorld == null) {
			if (subWorldGroup() == SubWorldGroup.RESOURCE)
				rtpWorld = RTPWorld.RESOURCE;
			else
				rtpWorld = RTPWorld.SURVIVAL;
		}

		final RTPWorld overworld = rtpWorld;
		final World world = overworld.getWorld();

		if (!running) {
			send(PREFIX + "Teleporting to random location");
			running = true;
		}

		count.getAndIncrement();

		int range = 250;
		List<Location> locationList = LocationUtils.getRandomPoints(world, 10);

		locationList.sort(Comparator.comparingInt(loc -> (int) (getDensity(loc, range) * 100000)));
		Location best = locationList.get(0);

		if (service.getProtectionsInRange(best, 50).size() != 0 && count.get() < 5) {
			rtp(overworld);
			return;
		}

		// TODO 1.19 Improve logic to handle other regions like warps/towns (Check if can build?)
		if (new WorldGuardUtils(world).getRegionNamesAt(best).contains("spawn")) {
			rtp(overworld);
			return;
		}

		best.getWorld().getChunkAtAsync(best, true).thenAccept(chunk -> {
			Block highestBlock = world.getHighestBlockAt(best);
			if (!highestBlock.getType().isSolid() && count.get() < 10) {
				Tasks.async(() -> rtp(overworld));
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
