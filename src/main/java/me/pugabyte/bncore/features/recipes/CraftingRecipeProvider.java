package me.pugabyte.bncore.features.recipes;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.RandomUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CraftingRecipeProvider extends MenuUtils implements InventoryProvider {

	CraftingMenuType menu;
	int[] inputSlots = {2, 3, 4, 11, 12, 13, 20, 21, 22};

	public CraftingRecipeProvider(CraftingMenuType menu) {
		this.menu = menu;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		switch (menu) {
			case MAIN:
				addCloseItem(contents);
				int row = 1;
				int column = 1;
				for (CraftingMenuType type : CraftingMenuType.values()) {
					if (type == CraftingMenuType.MAIN) continue;
					contents.set(row, column, ClickableItem.from(type.getItem(), e -> CraftingRecipeMenu.open(type, player)));
					if (column == 8) {
						column = 1;
						row++;
					} else column++;
				}
				break;
			default:
				contents.fill(ClickableItem.empty(new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).name(" ").build()));
				for (int i : inputSlots)
					contents.set(i, ClickableItem.NONE);
				addBackItem(contents, e -> CraftingRecipeMenu.open(CraftingMenuType.MAIN, player));
		}
	}

	public int ticks = 0;
	public int index = 0;

	@Override
	public void update(Player player, InventoryContents contents) {
		if (menu == CraftingMenuType.MAIN) return;
		ticks++;
		if (ticks == 20) ticks = 0;
		if (ticks != 1) return;
		index++;
		if (index >= menu.getList().size()) index = 0;
		CraftingRecipeMenu.CraftingRecipe recipe = menu.getList().get(index);
		for (int i : inputSlots)
			contents.set(i, ClickableItem.NONE);
		if (recipe.getChoice() != null && menu != CraftingMenuType.BEDS) {
			ItemStack item = new ItemStack(RandomUtils.randomElement(recipe.getChoice().getChoices()));
			contents.fillRect(0, 2, 2, 4, ClickableItem.empty(item));
			contents.set(1, 3, ClickableItem.empty(new ItemStack(recipe.getIngredient())));
			contents.set(1, 6, ClickableItem.empty(new ItemStack(recipe.getOutput(), recipe.getOutputAmount())));
		} else if (menu == CraftingMenuType.BEDS) {
			contents.set(inputSlots[0], ClickableItem.empty(new ItemStack(RandomUtils.randomElement(recipe.getChoice().getChoices()))));
			contents.set(inputSlots[1], ClickableItem.empty(new ItemStack(recipe.getIngredient())));
			contents.set(1, 6, ClickableItem.empty(new ItemStack(recipe.getOutput(), recipe.getOutputAmount())));
		} else {
			for (int i = 0; i < recipe.getIngredientAmount(); i++) {
				contents.set(inputSlots[i], ClickableItem.empty(new ItemStack(recipe.getIngredient())));
				contents.set(1, 6, ClickableItem.empty(new ItemStack(recipe.getOutput(), recipe.getOutputAmount())));
			}
		}


	}
}
