package gg.projecteden.nexus.features.recipes.models.builders;

import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import org.bukkit.Material;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.StonecuttingRecipe;
import org.jetbrains.annotations.NotNull;

public class StoneCutterBuilder extends RecipeBuilder<StoneCutterBuilder> {
	Material material;

	public StoneCutterBuilder(Material material) {
		this.ingredientIds.add(material.name());
		this.material = material;
	}

	@Override
	protected String getKey() {
		return "stonecutter_" + super.getKey();
	}

	@Override
	public NexusRecipe build() {
		return super.build().type(RecipeType.STONECUTTER);
	}

	@NotNull
	@Override
	public Recipe getRecipe() {
		return new StonecuttingRecipe(key(), result, material);
	}
}
