package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.features.holidays.bearfair20.models.RecipeObject;
import me.pugabyte.bncore.features.holidays.bearfair20.quests.Recipes;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class AllRecipesCommand extends CustomCommand {

	public AllRecipesCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommandAsConsole("minecraft:recipe give " + player().getName() + " *");

		for (RecipeObject recipe : Recipes.recipes)
			runCommandAsConsole("minecraft:recipe take " + player().getName() + " bncore:custom_bearfair_" + recipe.getKey());
	}

}
