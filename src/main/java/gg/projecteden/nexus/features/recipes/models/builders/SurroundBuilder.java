package gg.projecteden.nexus.features.recipes.models.builders;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

import static gg.projecteden.nexus.features.recipes.CustomRecipes.choiceOf;

public class SurroundBuilder extends RecipeBuilder<SurroundBuilder> {
	private final RecipeChoice center;
	private RecipeChoice surround;

	public SurroundBuilder(RecipeChoice center) {
		this.center = center;
	}

	public SurroundBuilder with(Tag<Material> surround) {
		return with(choiceOf(surround));
	}

	public SurroundBuilder with(Material surround) {
		return with(choiceOf(surround));
	}

	public SurroundBuilder with(ItemStack surround) {
		return with(choiceOf(surround));
	}

	public SurroundBuilder with(RecipeChoice surround) {
		this.surround = surround;
		return this;
	}

	@NotNull
	@Override
	public Recipe getRecipe() {
		return shaped("111", "121", "111")
			.add('1', surround)
			.add('2', center)
			.toMake(result)
			.id(id)
			.getRecipe();
	}

}
