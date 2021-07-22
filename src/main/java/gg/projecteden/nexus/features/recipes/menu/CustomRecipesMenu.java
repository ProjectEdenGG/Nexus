package gg.projecteden.nexus.features.recipes.menu;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.RandomUtils;
import lombok.NonNull;
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

	public static SmartInventory getMenu(RecipeType type) {
		return SmartInventory.builder()
				.title("Custom Recipes")
				.size(3, 9)
				.provider(new CustomRecipesProvider(type))
				.build();
	}

	public static void open(NexusRecipe recipe, Player player) {
		SmartInventory.builder()
				.title("Custom Recipes")
				.size(3, 9)
				.provider(new CustomRecipesProvider(recipe))
				.build()
				.open(player);
	}

	public static class CustomRecipesProvider extends MenuUtils implements InventoryProvider {

		@NonNull
		public RecipeType type;
		public NexusRecipe recipe;
		int[] inputSlots = {2, 3, 4, 11, 12, 13, 20, 21, 22};

		public CustomRecipesProvider(@NonNull RecipeType type) {
			this.type = type;
		}

		public CustomRecipesProvider(NexusRecipe recipe) {
			this.type = RecipeType.FUNCTIONAL;
			this.recipe = recipe;
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			switch (type) {
				case MAIN -> {
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
				}
				default -> {
					if (type == RecipeType.FUNCTIONAL && recipe == null) break;
					contents.fill(ClickableItem.empty(new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).name(" ").build()));
					for (int i : inputSlots)
						contents.set(i, ClickableItem.NONE);
					addBackItem(contents, e -> CustomRecipesMenu.open(RecipeType.MAIN, player));
				}
			}
			if (type == RecipeType.FUNCTIONAL) {
				if (recipe == null) {
					Pagination page = contents.pagination();
					addBackItem(contents, e -> CustomRecipesMenu.open(RecipeType.MAIN, player));
					List<NexusRecipe> recipes = type.getRecipes().stream().filter(nexusRecipe -> {
						if (nexusRecipe.getPermission() != null)
							return player.hasPermission(nexusRecipe.getPermission());
						return true;
					}).collect(Collectors.toList());
					ClickableItem[] clickableItems = new ClickableItem[recipes.size()];
					for (int i = 0; i < clickableItems.length; i++) {
						int j = i;
						clickableItems[i] = ClickableItem.from(recipes.get(j).getResult(), e -> CustomRecipesMenu.open(recipes.get(j), player));
					}
					page.setItems(clickableItems);
					page.setItemsPerPage(18);
					page.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

					// Arrows
					if (!page.isFirst())
						contents.set(0, 2, ClickableItem.from(new ItemBuilder(Material.ARROW).name("<-- Back").build(), e ->
								CustomRecipesMenu.getMenu(type).open(player, page.previous().getPage())));
					if (!page.isLast())
						contents.set(0, 6, ClickableItem.from(new ItemBuilder(Material.ARROW).name("Next -->").build(), e ->
								CustomRecipesMenu.getMenu(type).open(player, page.next().getPage())));
				} else {
					addBackItem(contents, e -> CustomRecipesMenu.open(RecipeType.FUNCTIONAL, player));
					addRecipeToMenu(contents, recipe);
				}
			}
		}

		public int ticks = 0;
		public int index = 0;

		@Override
		public void update(Player player, InventoryContents contents) {
			if (type == RecipeType.MAIN || type == RecipeType.FUNCTIONAL) return;

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
			addRecipeToMenu(contents, recipe);
		}

		public void addRecipeToMenu(InventoryContents contents, NexusRecipe recipe) {
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
