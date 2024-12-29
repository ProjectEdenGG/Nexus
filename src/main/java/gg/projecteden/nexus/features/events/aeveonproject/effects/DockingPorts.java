package gg.projecteden.nexus.features.events.aeveonproject.effects;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.aeveonproject.APUtils;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeftRegionEvent;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DockingPorts implements Listener {
	public DockingPorts() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onEnterRegion_DockingPort(PlayerEnteredRegionEvent event) {
		Player player = event.getPlayer();
		if (!APUtils.isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (id.contains("dockingport") || id.contains("vent_door")) {
			player.setSprinting(true);
		}
	}

	@EventHandler
	public void onEnterRegion_DockingTube(PlayerEnteredRegionEvent event) {
		Player player = event.getPlayer();
		if (!APUtils.isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (id.contains("dockingtube") || id.contains("vent")) {
			PotionEffect potionEffect = new PotionEffectBuilder(PotionEffectType.SPEED)
				.infinite()
				.amplifier(15)
				.build();

			player.addPotionEffect(potionEffect);
		}
	}

	@EventHandler
	public void onExitRegion_DockingTube(PlayerLeftRegionEvent event) {
		Player player = event.getPlayer();
		if (!APUtils.isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (id.contains("dockingtube") || id.contains("vent")) {
			player.removePotionEffect(PotionEffectType.SPEED);
		}
	}

}
