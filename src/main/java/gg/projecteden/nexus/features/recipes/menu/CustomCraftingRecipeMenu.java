package gg.projecteden.nexus.features.recipes.menu;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.recipes.functionals.backpacks.IronBackpack;
import gg.projecteden.nexus.features.recipes.menu.common.ICustomRecipeMenu;
import gg.projecteden.nexus.features.recipes.menu.common.ICustomRecipesMenu;
import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

@Title("&f麖ꈉ魁")
public class CustomCraftingRecipeMenu extends ICustomRecipeMenu {

	public CustomCraftingRecipeMenu(NexusRecipe recipe) {
		super(recipe);
	}

	public CustomCraftingRecipeMenu(NexusRecipe recipe, ICustomRecipesMenu previousMenu) {
		super(recipe, previousMenu);
	}

	protected void show(NexusRecipe recipe) {
		if (recipe.getRecipe() instanceof ShapedRecipe shaped) {
			if (shaped.getShape().length == 3)
				for (int i = 0; i < 9; i++) {
					char c = shaped.getShape()[i / 3].toCharArray()[i % 3];
					if (c == ' ')
						continue;

					contents.set(MATRIX_SLOTS[i], ClickableItem.empty(random(shaped.getChoiceMap().get(c))));
				}
			else if (shaped.getShape().length == 2)
				for (int i = 0; i < 4; i++) {
					char c = shaped.getShape()[i / 2].toCharArray()[i % 2];
					if (c == ' ')
						continue;
					contents.set(MATRIX_SLOTS[i >= 2 ? i + 1 : i], ClickableItem.empty(random(shaped.getChoiceMap().get(c))));
				}

			if (recipe.getType() == RecipeType.BACKPACKS && recipe instanceof IronBackpack ironBackpack)
				contents.set(MATRIX_SLOTS[4], ClickableItem.empty(ironBackpack.getPreviousBackpack()));
		} else if (recipe.getRecipe() instanceof ShapelessRecipe shapeless) {
			int slot = 0;
			for (RecipeChoice choice : shapeless.getChoiceList())
				contents.set(MATRIX_SLOTS[slot++], ClickableItem.empty(random(choice)));
		}
	}

}
