package gg.projecteden.nexus.features.recipes;

import gg.projecteden.nexus.features.events.y2020.bearfair20.models.RecipeObject;
import gg.projecteden.nexus.features.events.y2020.bearfair20.quests.Recipes;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Redirects.Redirect;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.recipes.RecipeUser;
import gg.projecteden.nexus.models.recipes.RecipeUserService;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NoArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@NoArgsConstructor
@Redirect(from = "/allrecipes", to = "/recipes all")
public class RecipesCommand extends CustomCommand implements Listener {
	private final RecipeUserService service = new RecipeUserService();
	private RecipeUser recipeUser;

	public RecipesCommand(CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			recipeUser = service.get(player());
	}

	public static void learnAll(String player) {
		PlayerUtils.runCommandAsConsole("minecraft:recipe give " + player + " *");

		for (RecipeObject recipe : Recipes.recipes)
			PlayerUtils.runCommandAsConsole("minecraft:recipe take " + player + " nexus:custom_bearfair_" + recipe.getKey());
	}

	public static void unlearnAll(String player) {
		PlayerUtils.runCommandAsConsole("minecraft:recipe take " + player + " *");
		// TODO Advancements?
	}

	@Path("all")
	@Description("Learn all recipes")
	void run() {
		learnAll(name());

		send(PREFIX + "You have learned all recipes");
		recipeUser.setAutoLearnAll(true);
		service.save(recipeUser);
	}

	@Path("none")
	@Description("Unlearn all recipes")
	void none() {
		unlearnAll(name());
		send(PREFIX + "You have unlearned all recipes");
		recipeUser.setAutoLearnAll(false);
		service.save(recipeUser);
	}

	@EventHandler
	public void on(PlayerJoinEvent event) {
		if (!new RecipeUserService().get(event.getPlayer()).isAutoLearnAll())
			return;

		learnAll(event.getPlayer().getName());
	}

}
