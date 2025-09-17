package gg.projecteden.nexus.features.customenchants.enchants;

import gg.projecteden.nexus.features.customenchants.models.CustomEnchant;
import io.papermc.paper.entity.Shearable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerShearEntityEvent;

public class FlockShearEnchant extends CustomEnchant implements Listener {

	@Override
	public int getMaxLevel() {
		return 5;
	}

	@EventHandler
	public void on(PlayerShearEntityEvent event) {
		var entity = event.getEntity();
		if (!(entity instanceof Shearable))
			return;

		int level = getLevel(event.getItem());
		if (level == 0)
			return;

		var radius = 2 + level;
		var nearby = entity.getNearbyEntities(radius, radius, radius).stream()
			.filter(nearbyEntity -> nearbyEntity != entity && nearbyEntity.getType() == entity.getType())
			.map(nearbyEntity -> (Shearable) nearbyEntity)
			.toList();

		if (nearby.isEmpty())
			return;

		for (Shearable nearbyEntity : nearby)
			if (nearbyEntity.readyToBeSheared())
				nearbyEntity.shear();
	}
}
