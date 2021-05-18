package me.pugabyte.nexus.features.recipes;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.features.recipes.menu.CustomRecipesMenu;
import me.pugabyte.nexus.features.recipes.models.RecipeType;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.utils.StringUtils.pretty;

public class CustomRecipesCommand extends CustomCommand {

	public CustomRecipesCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void open() {
		CustomRecipesMenu.open(RecipeType.MAIN, player());
	}

	@Path("reload")
	@Permission("group.admin")
	void reload() {
		send(PREFIX + "Reloading all recipes...");
		int amount = CustomRecipes.getRecipes().size();
		CustomRecipes.getRecipes().forEach(nexusRecipe -> Bukkit.removeRecipe(((Keyed) nexusRecipe.getRecipe()).getKey()));
		CustomRecipes.getRecipes().clear();
		new CustomRecipes().onStart();
		send(PREFIX + "Successfully reloaded &e" + amount + "&3 custom recipes");
	}

	/**
	 * Wakka wanted me to make this for him, so I did so.
	 * It can easily dupe items and such, so I think we should keep
	 * the normal ways of adding uncrafting recipes rather than a
	 * menu to uncraft items.
	 */
	@Path("ingredients")
	@Permission("group.admin")
	void ingredients() {
		ItemStack item = getToolRequired();
		send(PREFIX + "Ingredients for " + pretty(item));

		for (List<ItemStack> ingredients : RecipeUtils.uncraft(item)) {
			line();
			send("(" + ingredients.size() + ") " + ingredients.stream().map(StringUtils::pretty).collect(Collectors.joining(", ")));
		}
	}

	@Path("uncraft")
	@Permission("group.admin")
	void uncraft() {
		SmartInventory.builder().title("Uncraft Menu").size(3, 9).provider(new UncraftMenu()).build().open(player());
	}

	public static class UncraftMenu extends MenuUtils implements InventoryProvider {

		public int[] uncraftingSlots = {4, 5, 6, 13, 14, 15, 22, 23, 24};

		@Override
		public void init(Player player, InventoryContents contents) {
			contents.fill(ClickableItem.empty(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build()));

			for (int i = 0; i < uncraftingSlots.length; i++)
				contents.set(uncraftingSlots[i], ClickableItem.NONE);

			contents.set(1, 2, ClickableItem.from(new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).name("Place Item Here").build(), e -> {
				InventoryClickEvent clickEvent = (InventoryClickEvent) e.getEvent();
				if (ItemUtils.isNullOrAir(clickEvent.getWhoClicked().getItemOnCursor())) {
					for (int i = 0; i < uncraftingSlots.length; i++)
						contents.set(uncraftingSlots[i], ClickableItem.NONE);
					return;
				}
				Tasks.wait(2, () -> {
					List<List<ItemStack>> recipes = RecipeUtils.uncraft(clickEvent.getWhoClicked().getItemOnCursor());
					clickEvent.getWhoClicked().setItemOnCursor(null);
					getIndex(recipes, 0, contents);
				});
			}));

		}

		public void getIndex(List<List<ItemStack>> items, int index, InventoryContents contents) {
			for (int i = 0; i < uncraftingSlots.length; i++)
				contents.set(uncraftingSlots[i], ClickableItem.NONE);

			contents.set(2, 3, ClickableItem.empty(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build()));
			contents.set(2, 7, ClickableItem.empty(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build()));

			if (items.size() == 0) return;

			for (int i = 0; i < items.get(index).size(); i++) {
				ItemStack item = items.get(index).get(i);
				if (ItemUtils.isNullOrAir(item))
					item = new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).name("Air").build();
				contents.set(uncraftingSlots[i], ClickableItem.empty(item));
			}

			if (index != 0) {
				contents.set(2, 3, ClickableItem.from(new ItemBuilder(Material.ARROW).name("Previous").build(), e -> {
					getIndex(items, index - 1, contents);
				}));
			}
			if (index != items.size() - 1) {
				contents.set(2, 7, ClickableItem.from(new ItemBuilder(Material.ARROW).name("Next").build(), e -> {
					getIndex(items, index + 1, contents);
				}));
			}
		}

	}


}
