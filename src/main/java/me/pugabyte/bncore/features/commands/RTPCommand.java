package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Async;
import me.pugabyte.bncore.framework.commands.models.annotations.Cooldown;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.lwc.LWCProtection;
import me.pugabyte.bncore.models.lwc.LWCProtectionService;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Aliases({"randomtp", "wild"})
public class RTPCommand extends CustomCommand {

	public RTPCommand(CommandEvent event) {
		super(event);
	}

	LWCProtectionService service = new LWCProtectionService();
	AtomicInteger count = new AtomicInteger(0);
	boolean running = false;

	@Path
	@Async
	@Cooldown(30 * 20)
	void rtp() {
		if (!Arrays.asList("world", "survival", "legacy_survival").contains(player().getWorld().getName()))
			error("You must be in the survival world to run this command");

		if (!running) {
			send(PREFIX + "Teleporting to random location");
			running = true;
		}
		count.getAndIncrement();
		int worldRange = 10000;
		int range = 250;
		List<Location> locationList = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			locationList.add(new Location(player().getWorld(), (Math.random() * (worldRange * 2) - worldRange), 70, (Math.random() * (worldRange * 2) - worldRange)));
		}
		locationList.sort(Comparator.comparingInt(loc -> (int) (getDensity(loc, range) * 100000)));
		Location best = locationList.get(0);
		if (service.getProtectionsInRange(best, 50).size() != 0 && count.get() < 5) {
			rtp();
			return;
		}
		Tasks.sync(() -> {
			Block highestBlock = player().getWorld().getHighestBlockAt((int) best.getX(), (int) best.getZ()).getLocation().subtract(0, 1, 0).getBlock();
			if (!highestBlock.getType().isSolid() && count.get() < 10) {
				Tasks.async(this::rtp);
				return;
			}
			player().teleport(highestBlock.getLocation().add(0, 1, 0));
		});
	}

	public double getDensity(Location location, int range) {
		List<LWCProtection> protections = service.getProtectionsInRange(location, range);
		return (protections.size() / Math.pow(range * 2.0, 2.0)) * 100;
	}
}