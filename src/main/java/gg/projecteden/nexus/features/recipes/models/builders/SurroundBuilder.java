package gg.projecteden.nexus.features.recipes.models.builders;

import gg.projecteden.nexus.features.recipes.CustomRecipes;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlockTag;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SurroundBuilder extends RecipeBuilder<SurroundBuilder> {
	private final RecipeChoice center;
	private RecipeChoice surround;

	SurroundBuilder(RecipeChoice center) {
		this.center = center;
	}

	public SurroundBuilder with(CustomBlockTag surround) {
		ingredientIds.add(CustomRecipes.keyOf(surround));
		return with(CustomRecipes.choiceOf(surround));
	}

	public SurroundBuilder with(Tag<Material> surround) {
		ingredientIds.add(CustomRecipes.keyOf(surround));
		return with(CustomRecipes.choiceOf(surround));
	}

	public SurroundBuilder with(CustomBlock surround) {
		ingredientIds.add(CustomRecipes.keyOf(surround));
		return with(CustomRecipes.choiceOf(surround));
	}

	public SurroundBuilder with(Material surround) {
		ingredientIds.add(CustomRecipes.keyOf(surround));
		return with(CustomRecipes.choiceOf(surround));
	}

	public SurroundBuilder with(ItemStack surround) {
		ingredientIds.add(CustomRecipes.keyOf(surround));
		return with(CustomRecipes.choiceOf(surround));
	}

	public SurroundBuilder with(List<ItemStack> surround) {
		return with(CustomRecipes.choiceOf(surround));
	}

	protected SurroundBuilder with(RecipeChoice surround) {
		this.surround = surround;
		return this;
	}

	@Override
	protected String getKey() {
		return "surround__" + ingredientIds.get(0) + "__with__" + ingredientIds.get(1) + resultId;
	}

	@NotNull
	@Override
	public Recipe getRecipe() {
		final ShapedBuilder builder = shaped("111", "121", "111")
			.add('1', surround)
			.add('2', center)
			.toMake(result);

		builder.ingredientIds.addAll(ingredientIds);

		return builder.getRecipe();
	}

}
