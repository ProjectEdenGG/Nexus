package gg.projecteden.nexus.features.listeners;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import static gg.projecteden.api.common.utils.StringUtils.camelCase;
import static gg.projecteden.nexus.features.resourcepack.models.CustomMaterial.GROWTH_RAY;
import static gg.projecteden.nexus.features.resourcepack.models.CustomMaterial.SHRINK_RAY;

public class ShrinkRay implements Listener {

	@EventHandler
	public void on(PlayerInteractEntityEvent event) {
		var player = event.getPlayer();

		if (!Rank.of(player).isStaff())
			return;

		var entity = event.getRightClicked();
		if (entity instanceof Player)
			return;

		if (!(entity instanceof LivingEntity livingEntity))
			return;

		if (!Restrictions.isPerkAllowedAt(player, entity.getLocation()))
			return;

		if (CitizensUtils.isNPC(entity))
			return;

		try {
			var tool = player.getInventory().getItem(event.getHand());
			var attribute = livingEntity.getAttribute(Attribute.SCALE);
			var entityType = camelCase(livingEntity.getType());

			if (SHRINK_RAY.is(tool) || GROWTH_RAY.is(tool)) {
				if (attribute == null)
					throw new InvalidInputException("Could not find scale attribute on " + entityType);

				double newValue = 0;

				if (SHRINK_RAY.is(tool)) {
					newValue = attribute.getBaseValue() - .1;
					if (newValue < .5)
						throw new InvalidInputException("That " + entityType + " is already the minimum size");
				}

				if (GROWTH_RAY.is(tool)) {
					newValue = attribute.getBaseValue() + .1;
					if (newValue > 1.5)
						throw new InvalidInputException("That " + entityType + " is already the maximum size");
				}

				if (newValue == 0)
					throw new InvalidInputException("Could not determine new size for " + entityType);

				attribute.setBaseValue(newValue);
			}
		} catch (Exception ex) {
			MenuUtils.handleException(player, StringUtils.getPrefix("Shrink Ray"), ex);
		}
	}

}
