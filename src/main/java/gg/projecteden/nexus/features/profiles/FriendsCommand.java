package gg.projecteden.nexus.features.profiles;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.friends.FriendsUser;
import gg.projecteden.nexus.models.friends.FriendsUserService;
import lombok.NonNull;

public class FriendsCommand extends CustomCommand {
	static FriendsUserService userService = new FriendsUserService();
	FriendsUser user;

	public FriendsCommand(@NonNull CommandEvent event) {
		super(event);

		if (isPlayerCommandEvent())
			user = userService.get(player());
	}

	@Path
	void run() {
		send(PREFIX + "Coming soon!");
	}

}
