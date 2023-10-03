package gg.projecteden.nexus.features.homes;

import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.home.Home;
import gg.projecteden.nexus.models.home.HomeOwner;
import gg.projecteden.nexus.models.home.HomeService;
import lombok.NonNull;

import java.util.Optional;

public class SetHomeCommand extends CustomCommand {
	private final HomeService service = new HomeService();
	private HomeOwner homeOwner;

	public SetHomeCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = HomesFeature.PREFIX;
		if (isPlayerCommandEvent())
			homeOwner = service.get(player());
	}

	@Path("[name]")
	@Description("Set a new home or update a home's location")
	void setHome(@Arg("home") String homeName) {
		Optional<Home> home = homeOwner.getHome(homeName);

		if (home.isPresent()) {
			ConfirmationMenu.builder()
				.title("Move home &3" + homeName + "&4?")
				.onConfirm(e -> {
					home.get().setLocation(location());
					service.save(homeOwner);
					send(PREFIX + "Updated location of home \"&e" + homeName + "&3\"");
				})
				.open(player());
		} else {
			homeOwner.checkHomesLimit();
			homeOwner.add(Home.builder()
					.uuid(homeOwner.getUuid())
					.name(homeName)
					.location(location())
					.build());
			service.save(homeOwner);
			send(PREFIX + "Home \"&e" + homeName + "&3\" set to current location. Return with &c/h" + (homeName.equalsIgnoreCase("home") ? "" : " " + homeName));
		}
	}

	@Permission(Group.MODERATOR)
	@Path("<player> <name>")
	@Description("Set a new home for another player or update a home's location")
	void setHome(HomeOwner homeOwner, String homeName) {
		Optional<Home> home = homeOwner.getHome(homeName);
		String message;
		if (home.isPresent()) {
			home.get().setLocation(location());
			message = "Updated location of home \"&e" + homeName + "&3\"";
		} else {
			homeOwner.add(Home.builder()
					.uuid(homeOwner.getUuid())
					.name(homeName)
					.location(location())
					.build());
			message = "Home \"&e" + homeName + "&3\" set to current location";
		}

		service.save(homeOwner);
		send(PREFIX + message);
	}

}
