package gg.projecteden.nexus.features.recipes.functionals;

import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelInstance;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

public class DiamondTotemOfUndying extends FunctionalRecipe {

	@Getter
	private static final ItemStack item = getCustomModel().getItem();

	public static ItemModelInstance getCustomModel() {
		return ItemModelInstance.of(Material.TOTEM_OF_UNDYING, ItemModelType.DIAMOND_TOTEM_OF_UNDYING.getModel());
	}

	@Override
	public ItemStack getResult() {
		return item;
	}

	@Override
	public @NotNull Recipe getRecipe() {
		return RecipeBuilder.surround(Material.TOTEM_OF_UNDYING)
			.with(Material.DIAMOND)
			.toMake(getResult())
			.getRecipe();
	}

	@EventHandler
	public void onEntityResurrect(EntityResurrectEvent event) {
		if (!event.isCancelled())
			return;

		if (!(event.getEntity() instanceof Player player))
			return;

		PlayerInventory inv = player.getInventory();

		final ItemStack item = PlayerUtils.searchInventory(player, getCustomModel());
		if (Nullables.isNullOrAir(item))
			return;

		event.setCancelled(false);
		// Delete the totem they have
		inv.removeItemAnySlot(item);
		// Move item from offhand
		ItemStack offHand = inv.getItemInOffHand().clone();
		Tasks.wait(1, () -> inv.setItemInOffHand(offHand));

		// Put a totem in their offhand
		inv.setItemInOffHand(item);
		Tasks.wait(2, player::updateInventory);
	}
}
