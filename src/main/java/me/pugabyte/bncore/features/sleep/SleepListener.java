package me.pugabyte.bncore.features.sleep;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;

import static me.pugabyte.bncore.BNCore.sleep;

public class SleepListener implements Listener {

	public SleepListener() {
		BNCore.registerListener(this);
	}

	@EventHandler
	public void onBedEnter(PlayerBedEnterEvent event) {
		if (!sleep.isHandling()) {
			Utils.wait(1, () -> sleep.calculate(event.getPlayer().getWorld()));
		}
	}

	@EventHandler
	public void onBedLeave(PlayerBedLeaveEvent event) {
		if (!sleep.isHandling()) {
			World world = event.getPlayer().getWorld();
			if (world.getTime() >= 12541 && world.getTime() <= 23458) {
				Utils.wait(1, () -> sleep.calculate(world));
			}
		}
	}

}