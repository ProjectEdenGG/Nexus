package gg.projecteden.nexus.features.events.aeveonproject.effects;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeftRegionEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static gg.projecteden.nexus.features.events.aeveonproject.APUtils.isInWorld;

public class DockingPorts implements Listener {
	public DockingPorts() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onEnterRegion_DockingPort(PlayerEnteredRegionEvent event) {
		Player player = event.getPlayer();
		if (!isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (id.contains("dockingport") || id.contains("vent_door")) {
			player.setSprinting(true);
		}
	}

	@EventHandler
	public void onEnterRegion_DockingTube(PlayerEnteredRegionEvent event) {
		Player player = event.getPlayer();
		if (!isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (id.contains("dockingtube") || id.contains("vent")) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999999, 15, false, false));
		}
	}

	@EventHandler
	public void onExitRegion_DockingTube(PlayerLeftRegionEvent event) {
		Player player = event.getPlayer();
		if (!isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (id.contains("dockingtube") || id.contains("vent")) {
			player.removePotionEffect(PotionEffectType.SPEED);
		}
	}

}
