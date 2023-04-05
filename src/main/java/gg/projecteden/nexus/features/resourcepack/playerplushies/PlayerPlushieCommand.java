package gg.projecteden.nexus.features.resourcepack.playerplushies;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Permission(Group.ADMIN)
public class PlayerPlushieCommand extends CustomCommand {

	public PlayerPlushieCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		new PlayerPlushiePickerProvider().open(player());
	}

}
