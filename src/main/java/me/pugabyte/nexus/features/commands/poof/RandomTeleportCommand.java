package me.pugabyte.nexus.features.commands.poof;

import com.sk89q.worldedit.bukkit.paperlib.PaperLib;
import eden.utils.TimeUtils.Time;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Cooldown;
import me.pugabyte.nexus.framework.commands.models.annotations.Cooldown.Part;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.lwc.LWCProtection;
import me.pugabyte.nexus.models.lwc.LWCProtectionService;
import me.pugabyte.nexus.utils.LocationUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Aliases({"randomtp", "rtp", "wild"})
public class RandomTeleportCommand extends CustomCommand {
	LWCProtectionService service = new LWCProtectionService();
	AtomicInteger count = new AtomicInteger(0);
	boolean running = false;

	public RandomTeleportCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Async
	@Cooldown(value = @Part(value = Time.SECOND, x = 30), bypass = "group.admin")
	void rtp() {
		if (!Arrays.asList("world", "survival", "resource").contains(world().getName()))
			error("You must be in the survival world to run this command");

		if (!running) {
			send(PREFIX + "Teleporting to random location");
			running = true;
		}
		count.getAndIncrement();

		int radius = 0;
		switch (world().getName()) {
			case "world" -> radius = 17500;
			case "survival" -> radius = 7500;
			case "resource" -> radius = 2500;
			default -> error("Could not find world border of current world");
		}

		int range = 250;
		List<Location> locationList = LocationUtils.getRandomPointInCircle(world(), radius);

		locationList.sort(Comparator.comparingInt(loc -> (int) (getDensity(loc, range) * 100000)));
		Location best = locationList.get(0);
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

	public double getDensity(Location location, int range) {
		List<LWCProtection> protections = service.getProtectionsInRange(location, range);
		return (protections.size() / Math.pow(range * 2.0, 2.0)) * 100;
	}
}