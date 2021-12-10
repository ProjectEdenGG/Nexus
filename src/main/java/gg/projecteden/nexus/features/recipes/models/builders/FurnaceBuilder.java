package gg.projecteden.nexus.features.recipes.models.builders;

import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

public class FurnaceBuilder extends RecipeBuilder<FurnaceBuilder> {
	protected final Material smelt;
	protected float exp;
	protected int time;

	public FurnaceBuilder(Material smelt) {
		this.smelt = smelt;
	}

	public FurnaceBuilder exp(float exp) {
		this.exp = exp;
		return this;
	}

	public FurnaceBuilder time(int time) {
		this.time = time;
		return this;
	}

	@Override
	protected @NotNull String getKey() {
		return "furnace_" + super.getKey();
	}

	@Override
	public NexusRecipe build() {
		return super.build().type(RecipeType.FURNACE);
	}

	@NotNull
	@Override
	public Recipe getRecipe() {
		return new FurnaceRecipe(key(), result, smelt, exp, time);
	}

}
