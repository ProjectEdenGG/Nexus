package gg.projecteden.nexus.features.listeners;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MathUtils;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gg.projecteden.api.common.utils.StringUtils.camelCase;
import static gg.projecteden.nexus.features.resourcepack.models.ItemModelType.PROPORTIONATOR;

public class Proportionator implements Listener {
	private static final double MIN = .4;
	private static final double MAX = 1.6;
	private static final List<EntityType> DISABLED = List.of(EntityType.ENDER_DRAGON);

	private static final Map<EntityType, Double> MIN_OVERRIDES = new HashMap<>() {{
		put(EntityType.SPIDER, .3d);
	}};
	private static final Map<EntityType, Double> MAX_OVERRIDES = new HashMap<>() {{
		put(EntityType.RABBIT, 2d);
	}};

	public static double min(Entity entity) {
		return MIN_OVERRIDES.getOrDefault(entity.getType(), MIN);
	}

	public static double max(Entity entity) {
		return MAX_OVERRIDES.getOrDefault(entity.getType(), MAX);
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		var player = event.getPlayer();
		var tool = event.getItem();

		AttributeInstance entityInteractRange = player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE);
		int range = 3;
		if (entityInteractRange != null)
			range = (int) Math.ceil(entityInteractRange.getValue());

		if (player.getTargetEntity(range) != null)
			return;
		
		if (event.getHand() == EquipmentSlot.OFF_HAND)
			return;

		if (!PROPORTIONATOR.is(tool))
			return;

		if (!event.getAction().isRightClick())
			return;

		if (!player.isSneaking())
			return;

		if (!new CooldownService().check(player, "proportionator-mode", TickTime.SECOND))
			return;

		if (isShrinkMode(tool))
			tool = new ItemBuilder(tool).customModelData(10).build();
		else
			tool = new ItemBuilder(tool).customModelData(0).build();

		player.getInventory().setItemInMainHand(tool);
		event.setCancelled(true);
	}

	@EventHandler
	public void on(PlayerInteractEntityEvent event) {
		var player = event.getPlayer();
		var tool = player.getInventory().getItem(event.getHand());

		if (event.getHand() == EquipmentSlot.OFF_HAND)
			return;
		
		if (!PROPORTIONATOR.is(tool))
			return;
		
		var entity = event.getRightClicked();

		if (!(entity instanceof LivingEntity livingEntity))
			return;
		
		if (entity instanceof Player)
			return;
		
		if (!Restrictions.isPerkAllowedAt(player, entity.getLocation()))
			return;
		
		if (CitizensUtils.isNPC(entity))
			return;

		try {
			var attribute = livingEntity.getAttribute(Attribute.SCALE);
			var entityType = camelCase(livingEntity.getType());

			if (DISABLED.contains(livingEntity.getType()))
				throw new InvalidInputException("Scaling " + StringUtils.an(entityType) + " is not allowed");

			if (attribute == null)
				throw new InvalidInputException("Could not find scale attribute on " + entityType);

			double newValue = attribute.getBaseValue();

			if (isShrinkMode(tool)) {
				newValue -= .1;
				if (newValue < min(livingEntity))
					throw new InvalidInputException("That " + entityType + " is already the minimum size");
			} else {
				newValue += .1;
				if (newValue > max(livingEntity))
					throw new InvalidInputException("That " + entityType + " is already the maximum size");
			}

			if (newValue == 0)
				throw new InvalidInputException("Could not determine new size for " + entityType);

			// TODO fuel

			attribute.setBaseValue(newValue);
		} catch (InvalidInputException ex) {
			MenuUtils.handleException(player, StringUtils.getPrefix(Proportionator.class), ex);
		}
	}

	public boolean isShrinkMode(ItemStack tool) {
		return MathUtils.isBetween(new ItemBuilder(tool).customModelData(), 0, 4);
	}

	public boolean isGrowMode(ItemStack tool) {
		return MathUtils.isBetween(new ItemBuilder(tool).customModelData(), 10, 14);
	}

}




