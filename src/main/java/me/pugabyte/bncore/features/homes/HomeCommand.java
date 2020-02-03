package me.pugabyte.bncore.features.homes;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.homes.Home;
import me.pugabyte.bncore.models.homes.HomeOwner;
import me.pugabyte.bncore.models.homes.HomeService;
import org.bukkit.OfflinePlayer;

import java.util.List;

@Aliases("h")
public class HomeCommand extends CustomCommand {
	HomeService service;
	HomeOwner homeOwner;

	public HomeCommand(CommandEvent event) {
		super(event);
		service = new HomeService();
		homeOwner = service.get(player());
	}

	@Path("<home>")
	void teleport(@Arg(value = "home", tabCompleter = Home.class) String name) {
		Home home = homeOwner.getHome(name);
		if (home == null)
			if (arg(1) != null && arg(1).length() >= 3 && isPlayerArg(1)) {
				teleport(playerArg(1), convertToHome("home", playerArg(1)));
				return;
			} else
				error("You do not have a home named &e" + name);

		home.teleport(player());
	}

	@Path("<player> <home>")
	void teleport(OfflinePlayer player, @Arg(contextArg = 1) Home home) {
		home.teleport(player());
	}

	@ConverterFor(Home.class)
	public Home convertToHome(String value, OfflinePlayer context) {
		if (context == null) context = player();
		return ((HomeOwner) service.get(context)).getHome(value);
	}

	@TabCompleterFor(Home.class)
	public List<String> tabCompleteHome(String filter, OfflinePlayer context) {
		if (context == null) context = player();
		return ((HomeOwner) service.get(context)).getNames();
	}

}
