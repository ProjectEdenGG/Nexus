package me.pugabyte.nexus.features.recipes;

import me.pugabyte.nexus.features.recipes.menu.CustomRecipesMenu;
import me.pugabyte.nexus.features.recipes.models.RecipeType;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;

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


}
