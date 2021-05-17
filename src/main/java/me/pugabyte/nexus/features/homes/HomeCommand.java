package me.pugabyte.nexus.features.homes;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.home.Home;
import me.pugabyte.nexus.models.home.HomeOwner;
import me.pugabyte.nexus.models.home.HomeService;
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
		if (homeOwner.getHomes().size() == 0)
			error("You do not have any homes. Use /sethome [name] to create them");

		Optional<Home> home = homeOwner.getHome(name);
		if (!home.isPresent())
			error("You do not have a home named &e" + name);

		home.get().teleport(player());
	}

	@Path("<player> <home>")
	void teleport(OfflinePlayer player, @Arg(context = 1) Home home) {
		home.teleport(player());
	}

	@ConverterFor(Home.class)
	Home convertToHome(String value, OfflinePlayer context) {
		if (context == null) context = player();
		return service.get(context).getHome(value).orElseThrow(() -> new InvalidInputException("That home does not exist"));
	}

	@TabCompleterFor(Home.class)
	public List<String> tabCompleteHome(String filter, OfflinePlayer context) {
		if (context == null) context = player();
		return service.get(context).getNames(filter);
	}

}
