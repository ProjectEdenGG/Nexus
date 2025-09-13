package gg.projecteden.nexus.features.homes;

import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.home.Home;
import gg.projecteden.nexus.models.home.HomeOwner;
import gg.projecteden.nexus.models.home.HomeService;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.Optional;

@Aliases("h")
public class HomeCommand extends CustomCommand {
	private final HomeService service = new HomeService();
	private HomeOwner homeOwner;

	public HomeCommand(CommandEvent event) {
		super(event);
		PREFIX = HomesFeature.PREFIX;
		if (isPlayerCommandEvent())
			homeOwner = service.get(player());
	}

	@Path("[home]")
	@Description("Teleport to one of your homes")
	void teleport(@Arg(value = "home", tabCompleter = Home.class) String name) {
		if (homeOwner.getHomes().size() == 0)
			error("You do not have any homes. Use /sethome [name] to create them");

		Optional<Home> home = homeOwner.getHome(name);
		if (home.isEmpty())
			error("You do not have a home named &e" + name);

		home.get().teleportAsync(player());
	}

	@Path("<player> <home>")
	@Description("Teleport to another player's homes")
	void teleport(OfflinePlayer player, @Arg(context = 1) Home home) {
		home.teleportAsync(player());
	}

	@ConverterFor(Home.class)
	Home convertToHome(String value, HasUniqueId context) {
		if (context == null) context = player();
		return service.get(context).getHome(value).orElseThrow(() -> new InvalidInputException("That home does not exist"));
	}

	@TabCompleterFor(Home.class)
	public List<String> tabCompleteHome(String filter, HasUniqueId context) {
		if (context == null) context = player();
		return service.get(context).getNames(filter);
	}

}
