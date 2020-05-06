package me.pugabyte.bncore.features.recipes;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.Bukkit;

@Permission("group.admin")
public class CustomRecipesCommand extends CustomCommand {

	public CustomRecipesCommand(CommandEvent event) {
		super(event);
	}

	@Path("reload")
	void reload() {
		send(PREFIX + "Reloading all recipes...");
		CustomRecipes.recipes.keySet().forEach(Bukkit::removeRecipe);
		CustomRecipes.recipes.clear();
		CustomRecipes.amount = 0;
		new CustomRecipes();
		send(PREFIX + "Successfully reloaded " + CustomRecipes.amount + " custom recipes");
	}

}
