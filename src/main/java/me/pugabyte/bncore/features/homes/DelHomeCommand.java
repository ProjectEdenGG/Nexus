package me.pugabyte.bncore.features.homes;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.home.Home;
import me.pugabyte.bncore.models.home.HomeOwner;
import me.pugabyte.bncore.models.home.HomeService;
import org.bukkit.OfflinePlayer;

public class DelHomeCommand extends CustomCommand {
	HomeService service = new HomeService();
	HomeOwner homeOwner;

	public DelHomeCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = HomesFeature.PREFIX;
		homeOwner = service.get(player());
	}

	@Path("<name>")
	void delhome(@Arg("home") Home home) {
		homeOwner.delete(home);
		service.save(homeOwner);

		send(PREFIX + "Home \"&e" + home.getName() + "&3\" deleted");
	}

	@Permission("group.staff")
	@Path("<name>")
	void delhome(OfflinePlayer player, @Arg(contextArg = 1) Home home) {
		homeOwner = service.get(player);
		homeOwner.delete(home);
		service.save(homeOwner);

		send(PREFIX + "Home \"&e" + home.getName() + "&3\" deleted");
	}

}
