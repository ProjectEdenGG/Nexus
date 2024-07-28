package gg.projecteden.nexus.features.recipes.menu.common;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;

import java.util.List;

import static gg.projecteden.api.common.utils.RandomUtils.randomElement;

@RequiredArgsConstructor
public abstract class ICustomRecipeMenu extends ICustomRecipesMenu {
	protected final static int[] MATRIX_SLOTS = {2, 3, 4, 11, 12, 13, 20, 21, 22};
	protected final NexusRecipe recipe;

	public ICustomRecipeMenu(NexusRecipe recipe, ICustomRecipesMenu previousMenu) {
		super(previousMenu);
		this.recipe = recipe;
	}

	@Override
	public int getUpdateFrequency() {
		return 20;
	}

	private int index = 0;

	@Override
	public void update() {
		index++;
		List<NexusRecipe> recipes = recipe.getType().getRecipes().stream()
			.filter(nexusRecipe -> nexusRecipe.hasPermission(viewer))
			.filter(nexusRecipe -> {
				if (!recipe.getType().isFolder())
					return true;
				if (nexusRecipe == recipe)
					return true;
				if (recipe.getGroup() == null)
					return false;
				return recipe.getGroup().equals(nexusRecipe.getGroup());
			})
			.filter(NexusRecipe::isShowInMenu)
			.toList();

		if (recipes.isEmpty())
			return;

		if (index >= recipes.size())
			index = 0;

		NexusRecipe recipe = recipes.get(index);

		for (int i : MATRIX_SLOTS)
			contents.set(i, ClickableItem.NONE);

		contents.set(1, 7, ClickableItem.empty(recipe.getResult()));
		show(recipe);
	}

	abstract protected void show(NexusRecipe recipe);

	protected ItemStack random(RecipeChoice choice) {
		if (choice instanceof MaterialChoice materialChoice)
			return new ItemStack(randomElement(materialChoice.getChoices()));
		else if (choice instanceof ExactChoice exactChoice)
			return randomElement(exactChoice.getChoices());
		else
			return new ItemStack(Material.BARRIER);
	}

}
