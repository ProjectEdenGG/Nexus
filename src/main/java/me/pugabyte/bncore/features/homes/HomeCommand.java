package me.pugabyte.bncore.features.homes;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.models.home.Home;
import me.pugabyte.bncore.models.home.HomeOwner;
import me.pugabyte.bncore.models.home.HomeService;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.Optional;

@Aliases("h")
public class HomeCommand extends CustomCommand {
	HomeService service;
	HomeOwner homeOwner;

	public HomeCommand(CommandEvent event) {
		super(event);
		PREFIX = HomesFeature.PREFIX;
		service = new HomeService();
		homeOwner = service.get(player());
	}

	@Path("[home]")
	void teleport(@Arg(value = "home", tabCompleter = Home.class) String name) {
		Optional<Home> home = homeOwner.getHome(name);
		if (!home.isPresent())
			if (arg(1) != null && arg(1).length() >= 3 && isPlayerArg(1)) {
				teleport(playerArg(1), convertToHome("home", playerArg(1)));
				return;
			} else
				error("You do not have a home named &e" + name);

		home.get().teleport(player());
	}

	@Path("<player> [home]")
	void teleport(OfflinePlayer player, @Arg(contextArg = 1) Home home) {
		home.teleport(player());
	}

	@ConverterFor(Home.class)
	public Home convertToHome(String value, OfflinePlayer context) {
		if (context == null) context = player();
		return ((HomeOwner) service.get(context)).getHome(value).orElseThrow(() -> new InvalidInputException("That home does not exist"));
	}

	@TabCompleterFor(Home.class)
	public List<String> tabCompleteHome(String filter, OfflinePlayer context) {
		if (context == null) context = player();
		return ((HomeOwner) service.get(context)).getNames(filter);
	}

}
