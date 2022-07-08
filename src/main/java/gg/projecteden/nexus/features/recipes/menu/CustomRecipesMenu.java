package gg.projecteden.nexus.features.recipes.menu;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import gg.projecteden.nexus.features.recipes.models.RecipeGroup;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.*;

import static gg.projecteden.nexus.utils.RandomUtils.randomElement;

@Rows(3)
@RequiredArgsConstructor
public class CustomRecipesMenu extends InventoryProvider {
	private final RecipeType type;
	private final NexusRecipe recipe;
	private final CustomRecipesMenu previousMenu;
	private int page;

	private final static int[] MATRIX_SLOTS = {2, 3, 4, 11, 12, 13, 20, 21, 22};

	public CustomRecipesMenu(RecipeType type) {
		this(type, null, null);
	}

	public CustomRecipesMenu(NexusRecipe recipe) {
		this(recipe.getType(), recipe, null);
	}

	public CustomRecipesMenu(RecipeType type, CustomRecipesMenu previousMenu) {
		this(type, null, previousMenu);
	}

	public CustomRecipesMenu(NexusRecipe recipe, CustomRecipesMenu previousMenu) {
		this(recipe.getType(), recipe, previousMenu);
	}

	@Override
	public void open(Player player) {
		super.open(player, page);
	}

	@Override
	public String getTitle() {
		if (type == RecipeType.MAIN || (type.isFolder() && recipe == null))
			return "Custom Recipes";
		if (type == RecipeType.FURNACE)
			return "&f麖ꈉ糯";
		return "&f麖ꈉ魁";
	}

	@Override
	public void init() {
		this.page = contents.pagination().getPage();

		final List<ClickableItem> items = new ArrayList<>();

		if (type == RecipeType.MAIN) {
			addCloseItem();
			for (RecipeType type : RecipeType.values()) {
				if (type == RecipeType.MAIN)
					continue;
				if (type == RecipeType.STONECUTTER || type == RecipeType.CUSTOM_BLOCKS) // TODO Custom Blocks
					continue;

				items.add(ClickableItem.of(type.getItem(), e -> new CustomRecipesMenu(type, this).open(player)));
			}
		} else {
			if (type.isFolder()) {
				if (recipe == null) {
					Set<RecipeGroup> uniqueValues = new HashSet<>();
					for (NexusRecipe nexusRecipe : type.getRecipes()) {
						if (nexusRecipe.getPermission() != null && !player.hasPermission(nexusRecipe.getPermission())) {
							continue;
						}
						if (!nexusRecipe.isShowInMenu())
							continue;
						RecipeGroup group = nexusRecipe.getGroup();
						if (group != null) {
							if (uniqueValues.add(group)) {
								items.add(ClickableItem.of(new ItemBuilder(group.getDisplayItem()).name(group.getDisplayName()), e -> new CustomRecipesMenu(nexusRecipe, this).open(player)));
							}
						} else {
							items.add(ClickableItem.of(nexusRecipe.getResult(), e -> new CustomRecipesMenu(nexusRecipe, this).open(player)));
						}
					}
				} else {
					addRecipeToMenu(contents, recipe);
				}
			}
			addBackItem(e -> previousMenu.open(player));
		}

		paginator()
			.items(items)
			.previousSlot(0, 2)
			.nextSlot(0, 6)
			.perPage(18)
			.build();
	}

	private int ticks = 0;
	private int index = 0;

	@Override
	public void update() {
		if (type == RecipeType.MAIN || (type.isFolder() && recipe == null)) return;

		ticks++;
		if (ticks == 20)
			ticks = 0;

		if (ticks != 1)
			return;

		index++;
		List<NexusRecipe> recipes = type.getRecipes().stream()
			.filter(nexusRecipe -> nexusRecipe.hasPermission(player))
			.filter(nexusRecipe -> {
				if (!type.isFolder())
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

		addRecipeToMenu(contents, recipe);
	}

	public void addRecipeToMenu(InventoryContents contents, NexusRecipe recipe) {
		contents.set(1, 7, ClickableItem.empty(recipe.getResult()));
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
		} else if (recipe.getRecipe() instanceof ShapelessRecipe shapeless) {
			int slot = 0;
			for (RecipeChoice choice : shapeless.getChoiceList())
				contents.set(MATRIX_SLOTS[slot++], ClickableItem.empty(random(choice)));
		}
		else if (recipe.getRecipe() instanceof FurnaceRecipe smeltingRecipe) {
			contents.set(0, 3, ClickableItem.empty(smeltingRecipe.getInput()));
			contents.set(2, 3, ClickableItem.empty(random(new RecipeChoice.MaterialChoice(Material.COAL, Material.CHARCOAL, Material.LAVA_BUCKET, Material.BLAZE_ROD))));
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
