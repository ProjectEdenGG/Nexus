package gg.projecteden.nexus.features.homes;

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

public class DelHomeCommand extends CustomCommand {
	private final HomeService service = new HomeService();
	private HomeOwner homeOwner;

	public DelHomeCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = HomesFeature.PREFIX;
		if (isPlayerCommandEvent())
			homeOwner = service.get(player());
	}

	@Path("<name>")
	@Description("Delete a home")
	void delhome(@Arg("home") Home home) {
		homeOwner.delete(home);
		service.save(homeOwner);

		send(PREFIX + "Home \"&e" + home.getName() + "&3\" deleted");
	}

	// TODO: CAUSES INTERNAL SERVER ERROR, ICustomCommand#convert L339
	@Permission(Group.MODERATOR)
	@Path("<player> <name>")
	@Description("Delete another player's home")
	void delhome(HomeOwner homeOwner, @Arg(context = 1) Home home) {
		homeOwner.delete(home);
		service.save(homeOwner);

		send(PREFIX + "Home \"&e" + home.getName() + "&3\" deleted");
	}

}
