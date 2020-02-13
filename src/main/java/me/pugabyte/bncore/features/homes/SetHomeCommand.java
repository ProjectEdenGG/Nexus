package me.pugabyte.bncore.features.homes;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.homes.Home;
import me.pugabyte.bncore.models.homes.HomeOwner;
import me.pugabyte.bncore.models.homes.HomeService;

import java.util.Optional;

public class SetHomeCommand extends CustomCommand {
	HomeService service = new HomeService();
	HomeOwner homeOwner;

	public SetHomeCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = HomesFeature.PREFIX;
		homeOwner = service.get(player());
	}

	@Path("[name]")
	void sethome(@Arg("home") String homeName) {
		Optional<Home> home = homeOwner.getHome(homeName);
		String message;
		if (home.isPresent()) {
			home.get().setLocation(player().getLocation());
			message = "Updated location of home \"&e" + homeName + "&3\"";
		} else {
			homeOwner.add(Home.builder()
					.uuid(player().getUniqueId())
					.name(homeName)
					.location(player().getLocation())
					.build());
			message = "Home \"&e" + homeName + "&3\" set to current location. Return with &c/h" + (homeName.equalsIgnoreCase("home") ? "" : " " + homeName);
		}

		service.save(homeOwner);
		send(PREFIX + message);


	}

}
