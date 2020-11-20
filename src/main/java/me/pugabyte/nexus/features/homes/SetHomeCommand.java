package me.pugabyte.nexus.features.homes;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.home.Home;
import me.pugabyte.nexus.models.home.HomeOwner;
import me.pugabyte.nexus.models.home.HomeService;
import org.bukkit.OfflinePlayer;

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
	void setHome(@Arg("home") String homeName) {
		Optional<Home> home = homeOwner.getHome(homeName);

		int homes = homeOwner.getHomes().size();
		int max = homeOwner.getMaxHomes();
		int left = Math.max(0, max - homes);
		if (left <= 0 && !home.isPresent())
			error("You have used all of your available homes! &3To set more homes, you will need to either &erank up &3or &c/donate");

		String message;
		if (home.isPresent()) {
			home.get().setLocation(player().getLocation());
			message = "Updated location of home \"&e" + homeName + "&3\"";
		} else {
			homeOwner.add(Home.builder()
					.uuid(homeOwner.getUuid())
					.name(homeName)
					.location(player().getLocation())
					.build());
			message = "Home \"&e" + homeName + "&3\" set to current location. Return with &c/h" + (homeName.equalsIgnoreCase("home") ? "" : " " + homeName);
		}

		service.save(homeOwner);
		send(PREFIX + message);
	}

	@Permission("group.staff")
	@Path("<player> <name>")
	void setHome(OfflinePlayer player, String homeName) {
		homeOwner = service.get(player);

		Optional<Home> home = homeOwner.getHome(homeName);
		String message;
		if (home.isPresent()) {
			home.get().setLocation(player().getLocation());
			message = "Updated location of home \"&e" + homeName + "&3\"";
		} else {
			homeOwner.add(Home.builder()
					.uuid(homeOwner.getUuid())
					.name(homeName)
					.location(player().getLocation())
					.build());
			message = "Home \"&e" + homeName + "&3\" set to current location";
		}

		service.save(homeOwner);
		send(PREFIX + message);
	}

}
