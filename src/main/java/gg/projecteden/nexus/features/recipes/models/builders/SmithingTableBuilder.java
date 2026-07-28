package gg.projecteden.nexus.features.recipes.models.builders;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.jspecify.annotations.NonNull;

public class SmithingTableBuilder extends RecipeBuilder<SmithingTableBuilder> {

	private final ItemStack output;
	private Material input;
	private Material addition;
	private ItemStack template;

	public SmithingTableBuilder(ItemStack output) {
		this.output = output;
	}

	public SmithingTableBuilder base(Material item) {
		this.input = item;
		return this;
	}

	public SmithingTableBuilder addition(Material material) {
		this.addition = material;
		return this;
	}

	public SmithingTableBuilder template(ItemStack template) {
		this.template = template;
		return this;
	}

	@Override
	protected String getKey() {
		return "smithing_" + input.getKey().getKey() + "__and__" + template.getType().getKey().getKey() + resultId + output.getType().getKey();
	}

	@Override
	@NonNull Recipe getRecipe() {
		return new SmithingTransformRecipe(key(), output, new ExactChoice(template), new MaterialChoice(input), new MaterialChoice(addition));
	}
}
