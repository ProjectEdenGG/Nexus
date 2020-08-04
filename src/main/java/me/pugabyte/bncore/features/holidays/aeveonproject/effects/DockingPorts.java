package me.pugabyte.bncore.features.holidays.aeveonproject.effects;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.mewin.worldguardregionapi.events.RegionLeftEvent;
import me.pugabyte.bncore.BNCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static me.pugabyte.bncore.features.holidays.aeveonproject.AeveonProject.isInWorld;

public class DockingPorts implements Listener {
	public DockingPorts() {
		BNCore.registerListener(this);
	}

	@EventHandler
	public void onEnterRegion_DockingPort(RegionEnteredEvent event) {
		Player player = event.getPlayer();
		if (!isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (!id.contains("dockingport")) return;

		player.setSprinting(true);
	}

	@EventHandler
	public void onEnterRegion_DockingTube(RegionEnteredEvent event) {
		Player player = event.getPlayer();
		if (!isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (!id.contains("dockingtube")) return;

		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999999, 15, false, false));
	}

	@EventHandler
	public void onExitRegion_DockingTube(RegionLeftEvent event) {
		Player player = event.getPlayer();
		if (!isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (!id.contains("dockingtube")) return;

		player.removePotionEffect(PotionEffectType.SPEED);
	}

}
