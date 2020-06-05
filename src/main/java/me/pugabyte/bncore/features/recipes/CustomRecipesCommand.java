package me.pugabyte.bncore.features.recipes;

import fr.minuskube.inv.SmartInventory;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CustomRecipesCommand extends CustomCommand {

	public CustomRecipesCommand(CommandEvent event) {
		super(event);
	}


	@Path()
	void open() {
		openMenu(CraftingRecipeMenu.MAIN, player());
	}

	@Path("reload")
	@Permission("group.admin")
	void reload() {
		send(PREFIX + "Reloading all recipes...");
		CustomRecipes.recipes.keySet().forEach(Bukkit::removeRecipe);
		CustomRecipes.recipes.clear();
		CustomRecipes.amount = 0;
		new CustomRecipes();
		send(PREFIX + "Successfully reloaded &e" + CustomRecipes.amount + "&3 custom recipes");
	}

	public static void openMenu(CraftingRecipeMenu menu, Player player) {
		SmartInventory.builder()
				.size(3, 9)
				.title("Custom Recipes")
				.provider(new CraftingRecipeProvider(menu))
				.build()
				.open(player);
	}


}
