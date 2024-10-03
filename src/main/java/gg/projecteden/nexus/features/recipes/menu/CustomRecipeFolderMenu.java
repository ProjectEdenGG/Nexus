package gg.projecteden.nexus.features.recipes.menu;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.recipes.menu.common.ICustomRecipeMenu;
import gg.projecteden.nexus.features.recipes.menu.common.ICustomRecipesMenu;
import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import gg.projecteden.nexus.features.recipes.models.RecipeGroup;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Title("Custom Recipes")
public class CustomRecipeFolderMenu extends ICustomRecipesMenu {
	private RecipeType type;
	private int page;

	public CustomRecipeFolderMenu(RecipeType type, ICustomRecipesMenu previousMenu) {
		super(previousMenu);
		this.type = type;
	}

	@Override
	public void open(Player viewer) {
		super.open(viewer, page);
	}

	@Override
	public void init() {
		super.init();
		this.page = contents.pagination().getPage();

		final List<ClickableItem> items = new ArrayList<>();

		if (type == null) {
			for (RecipeType type : RecipeType.getEnabled()) {

				if (type.isFolder()) {
					if (type.getRecipes().isEmpty())
						continue;

					items.add(ClickableItem.of(type.getItem(), e -> new CustomRecipeFolderMenu(type, this).open(viewer)));
				} else
					items.add(ClickableItem.of(type.getItem(), e -> getMenu(type.getRecipes().get(0)).open(viewer)));
			}
		} else {
			if (type.isFolder()) {
				Set<RecipeGroup> uniqueValues = new HashSet<>();
				for (NexusRecipe nexusRecipe : type.getRecipes()) {
					if (nexusRecipe.getPermission() != null)
						if (!viewer.hasPermission(nexusRecipe.getPermission()))
							continue;

					if (!nexusRecipe.isShowInMenu())
						continue;

					RecipeGroup group = nexusRecipe.getGroup();
					if (group != null) {
						if (uniqueValues.add(group))
							items.add(ClickableItem.of(new ItemBuilder(group.getDisplayItem()).name(group.getDisplayName()), e -> getMenu(nexusRecipe).open(viewer)));
					} else {
						items.add(ClickableItem.of(nexusRecipe.getResult(), e -> getMenu(nexusRecipe).open(viewer)));
					}
				}
			}
		}

		paginator()
			.items(items)
			.previousSlot(0, 2)
			.nextSlot(0, 6)
			.perPage(18)
			.build();
	}

	public ICustomRecipeMenu getMenu(NexusRecipe recipe) {
		if (recipe.getRecipe() instanceof FurnaceRecipe)
			return new CustomFurnaceRecipeMenu(recipe, this);
		else
			return new CustomCraftingRecipeMenu(recipe, this);
	}

}
