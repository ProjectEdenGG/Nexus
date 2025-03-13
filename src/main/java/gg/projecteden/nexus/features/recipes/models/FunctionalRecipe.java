package gg.projecteden.nexus.features.recipes.models;

import gg.projecteden.nexus.Nexus;
import lombok.NonNull;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public abstract class FunctionalRecipe extends NexusRecipe implements Listener {

	public FunctionalRecipe() {
		Nexus.registerListener(this);
	}

	@Override
	public abstract ItemStack getResult();

	@NonNull
	@Override
	public abstract Recipe getRecipe();

	public RecipeType getRecipeType() {
		return RecipeType.FUNCTIONAL;
	}

	public void onStart() {}

}
