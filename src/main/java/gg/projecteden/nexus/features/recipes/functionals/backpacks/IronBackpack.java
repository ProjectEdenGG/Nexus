package gg.projecteden.nexus.features.recipes.functionals.backpacks;

import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.nexus.features.recipes.CustomRecipes;
import gg.projecteden.nexus.features.recipes.functionals.backpacks.Backpacks.BackpackTier;
import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Arrays;
import java.util.List;

public class IronBackpack extends FunctionalRecipe implements IBackpack {

	public static ItemStack result = BackpackTier.IRON.builder()
		.name("Iron Backpack")
		.build();

	@Override
	public ItemStack getItem() {
		return result;
	}

	@Override
	public ItemStack getResult() {
		return result;
	}

	@Override
	public RecipeType getRecipeType() {
		return RecipeType.BACKPACKS;
	}

	@Override
	public @NonNull Recipe getRecipe() {
		return RecipeBuilder.shaped("121", "232", "121")
			.add('1', Material.SHULKER_SHELL)
			.add('2', getUpgradeMaterial())
			.add('3', CustomRecipes.choiceOf(getPreviousBackpack().getType()))
			.toMake(getResult())
			.getRecipe();
	}

	public ItemStack getPreviousBackpack() {
		return Backpacks.getDefaultBackpack();
	}

	public Material getUpgradeMaterial() {
		return Material.IRON_INGOT;
	}

	public BackpackTier getTier() {
		return BackpackTier.IRON;
	}

	@EventHandler
	public void onCraftUpgrade(PrepareItemCraftEvent event) {
		if (!(event.getView().getPlayer() instanceof Player)) {
			return;
		}

		final ItemStack result = event.getInventory().getResult();
		if (!getResult().equals(result)) {
			return;
		}

		List<ItemStack> matrix = Arrays.stream(event.getInventory().getMatrix().clone())
			.filter(Nullables::isNotNullOrAir)
			.toList();

		ItemStack backpack = ItemUtils.find(matrix, Backpacks::isBackpack);
		if (backpack == null) {
			event.getInventory().setResult(null);
			return;
		}

		if (EnumUtils.previous(BackpackTier.class, getTier().ordinal()) != BackpackTier.of(backpack)) {
			event.getInventory().setResult(null);
			return;
		}

		event.getInventory().setResult(getTier().apply(backpack));
	}

}
