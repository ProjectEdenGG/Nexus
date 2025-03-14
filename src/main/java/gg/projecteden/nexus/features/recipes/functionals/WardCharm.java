package gg.projecteden.nexus.features.recipes.functionals;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.listeners.Restrictions;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static gg.projecteden.api.common.utils.StringUtils.camelCase;

public class WardCharm extends FunctionalRecipe {

	private static final String PREFIX = StringUtils.getPrefix(WardCharm.class);
	public static final NamespacedKey NBT_KEY = new NamespacedKey(Nexus.getInstance(), "WARD_CHARM");
	public static final List<EntityType> SUPPORTED_TYPES = List.of(
		EntityType.ALLAY,
		EntityType.ARMADILLO,
		EntityType.AXOLOTL,
		EntityType.BAT,
		EntityType.BEE,
		EntityType.COD,
		EntityType.DOLPHIN,
		EntityType.FOX,
		EntityType.FROG,
		EntityType.GLOW_SQUID,
		EntityType.GOAT,
		EntityType.OCELOT,
		EntityType.PANDA,
		EntityType.PARROT,
		EntityType.POLAR_BEAR,
		EntityType.RABBIT,
		EntityType.SALMON,
		EntityType.SNIFFER,
		EntityType.SNOW_GOLEM,
		EntityType.SQUID,
		EntityType.TROPICAL_FISH,
		EntityType.TURTLE
	);

	private static final List<DamageCause> ALLOWED_CAUSES = List.of(
		DamageCause.KILL,
		DamageCause.WORLD_BORDER,
		DamageCause.VOID,
		DamageCause.CRAMMING
	);

	@Getter
	private static final ItemStack item = new ItemBuilder(ItemModelType.GEM_SPINEL).name("Ward Charm").build();

	@Override
	public ItemStack getResult() {
		return item;
	}

	@Override
	public @NotNull Recipe getRecipe() {
		return RecipeBuilder.shaped("ALA", "GTW", "ANA")
			.add('A', Material.AMETHYST_SHARD)
			.add('L', Material.LEAD)
			.add('G', Material.GOLDEN_APPLE)
			.add('T', Material.TOTEM_OF_UNDYING)
			.add('W', Material.WOLF_ARMOR)
			.add('N', Material.NAME_TAG)
			.toMake(getResult())
			.getRecipe();
	}

	@EventHandler
	public void on(PlayerInteractEntityEvent event) {
		try {
			if (event.getHand() == EquipmentSlot.OFF_HAND)
				return;

			var player = event.getPlayer();
			var item = player.getInventory().getItemInMainHand();
			if (ItemModelType.of(item) != ItemModelType.GEM_SPINEL)
				return;

			var entity = event.getRightClicked();
			var entityTypeName = camelCase(entity.getType());
			if (!SUPPORTED_TYPES.contains(entity.getType()))
				throw new InvalidInputException("Cannot apply Ward Charm to " + entityTypeName);

			if (!Restrictions.isPerkAllowedAt(player, entity.getLocation()))
				throw new InvalidInputException("You cannot ward entities here");

			var pdc = entity.getPersistentDataContainer();
			if (pdc.has(NBT_KEY))
				throw new InvalidInputException("This " + entityTypeName + " is already warded");

			pdc.set(NBT_KEY, PersistentDataType.STRING, player.getUniqueId().toString());
			PlayerUtils.send(player, PREFIX + "Your " + entityTypeName + " is now warded");
			item.subtract();
		} catch (Exception ex) {
			MenuUtils.handleException(event.getPlayer(), PREFIX, ex);
		}
	}

	@EventHandler
	public void on(EntityDamageEvent event) {
		var entity = event.getEntity();

		if (!SUPPORTED_TYPES.contains(entity.getType()))
			return;

		if (ALLOWED_CAUSES.contains(event.getCause()))
			return;

		var pdc = entity.getPersistentDataContainer();
		if (!pdc.has(NBT_KEY, PersistentDataType.STRING))
			return;

		event.setCancelled(true);
	}
}
