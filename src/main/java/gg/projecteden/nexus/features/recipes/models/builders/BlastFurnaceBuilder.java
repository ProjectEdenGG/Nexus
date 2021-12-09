package gg.projecteden.nexus.features.recipes.models.builders;

import org.bukkit.Material;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

public class BlastFurnaceBuilder extends FurnaceBuilder {

	public BlastFurnaceBuilder(Material smelt) {
		super(smelt);
	}

	@Override
	public @NotNull Recipe getRecipe() {
		return new BlastingRecipe(key(), result, smelt, exp, time);
	}

}
