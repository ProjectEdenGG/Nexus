package me.pugabyte.nexus.features.events.aeveonproject.effects;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.mewin.worldguardregionapi.events.RegionLeftEvent;
import me.pugabyte.nexus.Nexus;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static me.pugabyte.nexus.features.events.aeveonproject.APUtils.isInWorld;

public class DockingPorts implements Listener {
	public DockingPorts() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onEnterRegion_DockingPort(RegionEnteredEvent event) {
		Player player = event.getPlayer();
		if (!isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (id.contains("dockingport") || id.contains("vent_door")) {
			player.setSprinting(true);
		}
	}

	@EventHandler
	public void onEnterRegion_DockingTube(RegionEnteredEvent event) {
		Player player = event.getPlayer();
		if (!isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (id.contains("dockingtube") || id.contains("vent")) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999999, 15, false, false));
		}
	}

	@EventHandler
	public void onExitRegion_DockingTube(RegionLeftEvent event) {
		Player player = event.getPlayer();
		if (!isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (id.contains("dockingtube") || id.contains("vent")) {
			player.removePotionEffect(PotionEffectType.SPEED);
		}
	}

}
