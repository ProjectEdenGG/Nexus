package gg.projecteden.nexus.features.recipes.menu;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.List;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.utils.RandomUtils.randomElement;

@Rows(3)
@Title("Custom Recipes")
@RequiredArgsConstructor
public class CustomRecipesMenu extends InventoryProvider {
	private final RecipeType type;
	private final NexusRecipe recipe;

	private final static int[] MATRIX_SLOTS = {2, 3, 4, 11, 12, 13, 20, 21, 22};

	public CustomRecipesMenu(RecipeType type) {
		this(type, null);
	}

	public CustomRecipesMenu(NexusRecipe recipe) {
		this(recipe.getType(), recipe);
	}

	@Override
	public void init() {
		switch (type) {
			case MAIN -> {
				addCloseItem();
				int row = 1;
				int column = 1;
				for (RecipeType type : RecipeType.values()) {
					if (type == RecipeType.MAIN || type == RecipeType.FURNACE) // TODO Support furnace recipes in menu
						continue;

					contents.set(row, column, ClickableItem.of(type.getItem(), e -> new CustomRecipesMenu(type).open(player)));

					if (column == 7) {
						column = 3;
						row++;
					} else
						column += 1;
				}
			}
			default -> {
				if (recipe == null && type.isFolder())
					break;

				contents.fill(ClickableItem.empty(new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).name(" ").build()));

				for (int slot : MATRIX_SLOTS)
					contents.set(slot, ClickableItem.NONE);

				addBackItem(e -> new CustomRecipesMenu(RecipeType.MAIN).open(player));
			}
		}

		if (type.isFolder()) {
			if (recipe == null) {
				addBackItem(e -> new CustomRecipesMenu(RecipeType.MAIN).open(player));

				paginator()
					.items(type.getRecipes().stream()
						.filter(nexusRecipe -> {
							if (nexusRecipe.getPermission() != null)
								return player.hasPermission(nexusRecipe.getPermission());
							return true;
						})
						.map(recipe -> ClickableItem.of(recipe.getResult(), e -> new CustomRecipesMenu(recipe).open(player)))
						.collect(Collectors.toList()))
					.previousSlot(0, 2)
					.nextSlot(0, 6)
					.build();
			} else {
				addBackItem(e -> new CustomRecipesMenu(type).open(player));
				addRecipeToMenu(contents, recipe);
			}
		}
	}

	private int ticks = 0;
	private int index = 0;

	@Override
	public void update() {
		if (type == RecipeType.MAIN || type.isFolder()) return;

		ticks++;
		if (ticks == 20)
			ticks = 0;

		if (ticks != 1)
			return;

		index++;
		List<NexusRecipe> recipes = type.getRecipes().stream()
			.filter(nexusRecipe -> nexusRecipe.hasPermission(player))
			.toList();

		if (recipes.isEmpty())
			return;

		if (index >= recipes.size())
			index = 0;

		NexusRecipe recipe = recipes.get(index);

		for (int i : MATRIX_SLOTS)
			contents.set(i, ClickableItem.NONE);

		addRecipeToMenu(contents, recipe);
	}

	public void addRecipeToMenu(InventoryContents contents, NexusRecipe recipe) {
		contents.set(1, 6, ClickableItem.empty(recipe.getResult()));
		if (recipe.getRecipe() instanceof ShapedRecipe shaped) {
			for (int i = 0; i < 9; i++) {
				char c = shaped.getShape()[i / 3].toCharArray()[i % 3];
				if (c == ' ')
					continue;

				contents.set(MATRIX_SLOTS[i], ClickableItem.empty(random(shaped.getChoiceMap().get(c))));
			}
		} else if (recipe.getRecipe() instanceof ShapelessRecipe shapeless) {
			int slot = 0;
			for (RecipeChoice choice : shapeless.getChoiceList())
				contents.set(MATRIX_SLOTS[slot++], ClickableItem.empty(random(choice)));
		}
	}

	private ItemStack random(RecipeChoice choice) {
		if (choice instanceof MaterialChoice materialChoice)
			return new ItemStack(randomElement(materialChoice.getChoices()));
		else if (choice instanceof ExactChoice exactChoice)
			return randomElement(exactChoice.getChoices());
		else
			return new ItemStack(Material.BARRIER);
	}

}
