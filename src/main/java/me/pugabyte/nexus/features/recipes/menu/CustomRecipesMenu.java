package me.pugabyte.nexus.features.recipes.menu;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.features.recipes.models.NexusRecipe;
import me.pugabyte.nexus.features.recipes.models.RecipeType;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.RandomUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomRecipesMenu {

	public static void open(RecipeType type, Player player) {
		SmartInventory.builder()
				.title("Custom Recipes")
				.size(3, 9)
				.provider(new CustomRecipesProvider(type))
				.build()
				.open(player);
	}

	@RequiredArgsConstructor
	public static class CustomRecipesProvider extends MenuUtils implements InventoryProvider {

		@NonNull
		public RecipeType type;
		int[] inputSlots = {2, 3, 4, 11, 12, 13, 20, 21, 22};

		@Override
		public void init(Player player, InventoryContents contents) {
			switch (type) {
				case MAIN:
					addCloseItem(contents);
					int row = 1;
					int column = 1;
					for (RecipeType type : RecipeType.values()) {
						if (type == RecipeType.MAIN) continue;
						contents.set(row, column, ClickableItem.from(type.getItem(), e -> CustomRecipesMenu.open(type, player)));
						if (column == 7) {
							column = 4;
							row++;
						} else column++;
					}
					break;
				default:
					contents.fill(ClickableItem.empty(new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).name(" ").build()));
					for (int i : inputSlots)
						contents.set(i, ClickableItem.NONE);
					addBackItem(contents, e -> CustomRecipesMenu.open(RecipeType.MAIN, player));
			}
		}

		public int ticks = 0;
		public int index = 0;

		@Override
		public void update(Player player, InventoryContents contents) {
			if (type == RecipeType.MAIN) return;
			ticks++;
			if (ticks == 20) ticks = 0;
			if (ticks != 1) return;
			index++;
			List<NexusRecipe> recipes = type.getRecipes().stream().filter(nexusRecipe -> {
				if (nexusRecipe.getPermission() != null)
					return player.hasPermission(nexusRecipe.getPermission());
				return true;
			}).collect(Collectors.toList());
			if (index >= recipes.size()) index = 0;
			NexusRecipe recipe = recipes.get(index);
			for (int i : inputSlots)
				contents.set(i, ClickableItem.NONE);
			contents.set(1, 6, ClickableItem.empty(recipe.getResult()));
			if (recipe.getPattern() != null) {
				Map<Character, ItemStack> characterItemStackMap = new HashMap<>();
				int items = 0;
				for (String row : recipe.getPattern())
					for (char c : row.toCharArray()) {
						if (c == ' ') continue;
						if (!characterItemStackMap.containsKey(c))
							if (c == '#' && recipe.getMaterialChoice() != null)
								characterItemStackMap.put(c, new ItemStack(RandomUtils.randomElement(recipe.getMaterialChoice().getChoices())));
							else
								characterItemStackMap.put(c, recipe.getIngredients().get(items++));
					}

				for (int i = 0; i < 9; i++) {
					char c = recipe.getPattern()[i / 3].toCharArray()[i % 3];
					if (c == ' ') continue;
					contents.set(inputSlots[i], ClickableItem.empty(characterItemStackMap.get(c)));
				}

			} else {
				int slot = 0;
				for (ItemStack item : recipe.getIngredients())
					contents.set(inputSlots[slot++], ClickableItem.empty(item));
				if (recipe.getMaterialChoice() != null)
					contents.set(inputSlots[slot], ClickableItem.empty(new ItemStack(RandomUtils.randomElement(recipe.getMaterialChoice().getChoices()))));
			}
		}
	}


}
