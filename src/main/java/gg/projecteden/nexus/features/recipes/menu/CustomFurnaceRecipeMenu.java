package gg.projecteden.nexus.features.recipes.menu;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.recipes.menu.common.ICustomRecipeMenu;
import gg.projecteden.nexus.features.recipes.menu.common.ICustomRecipesMenu;
import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import gg.projecteden.nexus.features.resourcepack.models.font.CustomTexture;
import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;

public class CustomFurnaceRecipeMenu extends ICustomRecipeMenu {

	public CustomFurnaceRecipeMenu(NexusRecipe recipe) {
		super(recipe);
	}

	public CustomFurnaceRecipeMenu(NexusRecipe recipe, ICustomRecipesMenu previousMenu) {
		super(recipe, previousMenu);
	}

	@Override
	public String getTitle() {
		return CustomTexture.GUI_SMELTING_RECIPE.getMenuTexture();
	}

	@Override
	protected void show(NexusRecipe recipe) {
		if (recipe.getRecipe() instanceof FurnaceRecipe smeltingRecipe) {
			contents.set(0, 3, ClickableItem.empty(smeltingRecipe.getInput()));
			contents.set(2, 3, ClickableItem.empty(random(new MaterialChoice(Material.COAL, Material.CHARCOAL, Material.LAVA_BUCKET, Material.BLAZE_ROD))));
		}
	}

}
