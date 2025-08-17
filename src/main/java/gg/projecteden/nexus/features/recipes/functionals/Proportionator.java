package gg.projecteden.nexus.features.recipes.functionals;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.listeners.Restrictions;
import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.proportionator.ProportionatorConfig;
import gg.projecteden.nexus.models.proportionator.ProportionatorConfigService;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MathUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import static gg.projecteden.api.common.utils.StringUtils.camelCase;
import static gg.projecteden.nexus.features.resourcepack.models.ItemModelType.PROPORTIONATOR;
import static gg.projecteden.nexus.features.resourcepack.models.ItemModelType.PROPORTIONATOR_CPU;
import static gg.projecteden.nexus.features.resourcepack.models.ItemModelType.PROPORTIONATOR_FUEL;
import static gg.projecteden.nexus.features.resourcepack.models.ItemModelType.PROPORTIONATOR_MOTHERBOARD;

public class Proportionator extends FunctionalRecipe {
	private static final ProportionatorConfigService service = new ProportionatorConfigService();

	public static final ItemBuilder ITEM = new ItemBuilder(PROPORTIONATOR)
		.name("&eProportionator")
		.lore("&3Crouch+Right Click the ground to swap modes")
		.lore("&3Right click entities to scale them")
		.loreize(false);

	private static final ItemBuilder CPU = new ItemBuilder(PROPORTIONATOR_CPU)
		.name("&eProportionator CPU");

	private static final ItemBuilder MOTHERBOARD = new ItemBuilder(PROPORTIONATOR_MOTHERBOARD)
		.name("&eProportionator Motherboard");

	private static final ItemBuilder FUEL = new ItemBuilder(PROPORTIONATOR_FUEL)
		.name("&eProportionator Fuel");

	public Proportionator() {
		// CPU
		RecipeBuilder.shaped("CCC", "CHC", "RGR")
			.add('C', Material.COPPER_BLOCK)
			.add('H', Material.HEAVY_CORE)
			.add('R', Material.RESIN_BRICK)
			.add('G', Glue.ITEM.get())
			.toMake(CPU.get())
			.register(RecipeType.FUNCTIONAL);

		// Fuel
		RecipeBuilder.shaped("ARA", "MDM", "ARA")
			.add('A', Material.AMETHYST_SHARD)
			.add('R', Material.RESIN_CLUMP)
			.add('M', Material.MAGMA_CREAM)
			.add('D', Material.DRAGON_BREATH)
			.toMake(FUEL.get())
			.register(RecipeType.FUNCTIONAL);
	}

	@Override
	public @NonNull Recipe getRecipe() {
		return RecipeBuilder.shaped("NSN", "ECE", "AMA")
			.add('N', Material.NETHERITE_INGOT)
			.add('S', Material.SPYGLASS)
			.add('E', Material.ECHO_SHARD)
			.add('C', CPU.get())
			.add('A', Material.AMETHYST_SHARD)
			.add('M', MOTHERBOARD.get())
			.toMake(getResult())
			.getRecipe();
	}

	@Override
	public ItemStack getResult() {
		return ITEM.get();
	}

	private static ProportionatorConfig config() {
		return service.get0();
	}

	public static double min(Entity entity) {
		return config().getMinOverrides().getOrDefault(entity.getType(), config().getMin());
	}

	public static double max(Entity entity) {
		return config().getMaxOverrides().getOrDefault(entity.getType(), config().getMax());
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

		if (CooldownService.isOnCooldown(player, "proportionator-mode", TickTime.SECOND))
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

			if (config().getDisabled().contains(livingEntity.getType()))
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

			if (player.getGameMode() != GameMode.CREATIVE) {
				if (!PlayerUtils.playerHas(player, PROPORTIONATOR_FUEL))
					throw new InvalidInputException("You do not have any Proportionator Fuel");

				if (!PlayerUtils.subtractItem(player, PROPORTIONATOR_FUEL))
					throw new InvalidInputException("Could not take any Proportionator Fuel from your inventory");
			}

			attribute.setBaseValue(newValue);
			ActionBarUtils.sendActionBar(player, "&e" + camelCase(entityType) + " scaled to " + StringUtils.getDf().format(newValue));
		} catch (InvalidInputException ex) {
			ActionBarUtils.sendActionBar(player, "&c" + ex.getMessage());
		}
	}

	public boolean isShrinkMode(ItemStack tool) {
		return MathUtils.isBetween(new ItemBuilder(tool).customModelData(), 0, 4);
	}

	public boolean isGrowMode(ItemStack tool) {
		return MathUtils.isBetween(new ItemBuilder(tool).customModelData(), 10, 14);
	}

}




