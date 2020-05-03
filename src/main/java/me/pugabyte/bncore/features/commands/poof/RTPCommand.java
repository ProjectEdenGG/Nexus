package me.pugabyte.bncore.features.commands.poof;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Async;
import me.pugabyte.bncore.framework.commands.models.annotations.Cooldown;
import me.pugabyte.bncore.framework.commands.models.annotations.Cooldown.Part;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.lwc.LWCProtection;
import me.pugabyte.bncore.models.lwc.LWCProtectionService;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Cooldown(value = @Part(value = Time.SECOND, x = 30), bypass = "group.admin")
@Aliases({"randomtp", "wild"})
public class RTPCommand extends CustomCommand {
	LWCProtectionService service = new LWCProtectionService();
	AtomicInteger count = new AtomicInteger(0);
	boolean running = false;

	public RTPCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Async
	void rtp() {
		if (!Arrays.asList("world", "survival", "resource").contains(player().getWorld().getName()))
			error("You must be in the survival world to run this command");

		if (!running) {
			send(PREFIX + "Teleporting to random location");
			running = true;
		}
		count.getAndIncrement();

		int radius = 0;
		switch (player().getWorld().getName()) {
			case "world": radius = 17500; break;
			case "survival": radius = 10000; break;
			case "resource": radius = 2500; break;
			default: error("Could not find world border of current world");
		}
		int range = 250;
		List<Location> locationList = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			double angle = Math.random() * Math.PI * 2;
			double r = Math.sqrt(Math.random());
			locationList.add(new Location(player().getWorld(), r * Math.cos(angle) * radius, 70, r * Math.sin(angle) * radius));
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
			player().teleport(highestBlock.getLocation().add(0, 1, 0), TeleportCause.COMMAND);
		});
	}

	public double getDensity(Location location, int range) {
		List<LWCProtection> protections = service.getProtectionsInRange(location, range);
		return (protections.size() / Math.pow(range * 2.0, 2.0)) * 100;
	}
}