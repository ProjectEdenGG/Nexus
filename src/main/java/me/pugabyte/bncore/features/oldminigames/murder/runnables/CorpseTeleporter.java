package me.pugabyte.bncore.features.oldminigames.murder.runnables;

import me.pugabyte.bncore.BNCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.scheduler.BukkitScheduler;

public class CorpseTeleporter {

	private ArmorStand armorStand;
	private Location location;
	private int counter = 1;
	private int taskId;

	public CorpseTeleporter(ArmorStand armorStand) {
		this.armorStand = armorStand;
		this.location = armorStand.getLocation();
		startTeleporter();
	}

	public void down() {
		location.setY(location.getY() - .1);
		counter++;
	}

	public void startTeleporter() {
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		taskId = scheduler.scheduleSyncRepeatingTask(BNCore.getInstance(), () -> {
			down();
			armorStand.teleport(location);
			if (counter == 16) {
				Bukkit.getScheduler().cancelTask(taskId);
			}
		}, 0L, 1L);
	}
}