package gg.projecteden.nexus.features.recipes.models;

import gg.projecteden.nexus.features.recipes.CustomRecipes;
import gg.projecteden.nexus.utils.Nullables;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class NexusRecipe {

	@NonNull
	public Recipe recipe;
	public RecipeType type = RecipeType.MISC;

	public String getPermission() {
		return null;
	}

	public ItemStack getResult() {
		return recipe.getResult();
	}

	public NexusRecipe type(RecipeType type) {
		this.type = type;
		return this;
	}

	public void register() {
		CustomRecipes.register(this);
	}

	public boolean hasPermission(Player player) {
		final String permission = getPermission();
		if (permission != null)
			return player.hasPermission(permission);
		return true;
	}

	@NotNull
	protected List<ItemStack> getFilteredMatrix(PrepareItemCraftEvent event) {
		List<ItemStack> matrix = new ArrayList<>(Arrays.asList(event.getInventory().getMatrix().clone()));
		matrix.removeIf(Nullables::isNullOrAir);
		return matrix;
	}

}
