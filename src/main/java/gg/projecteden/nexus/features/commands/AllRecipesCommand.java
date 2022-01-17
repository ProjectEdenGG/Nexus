package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.events.y2020.bearfair20.models.RecipeObject;
import gg.projecteden.nexus.features.events.y2020.bearfair20.quests.Recipes;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Description("Learn every recipe")
public class AllRecipesCommand extends CustomCommand {

	public AllRecipesCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommandAsConsole("minecraft:recipe give " + name() + " *");

		for (RecipeObject recipe : Recipes.recipes)
			runCommandAsConsole("minecraft:recipe take " + name() + " nexus:custom_bearfair_" + recipe.getKey());
	}

}
