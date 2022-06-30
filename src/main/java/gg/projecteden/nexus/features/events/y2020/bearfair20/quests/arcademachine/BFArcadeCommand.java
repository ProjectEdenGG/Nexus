package gg.projecteden.nexus.features.events.y2020.bearfair20.quests.arcademachine;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Permission(Group.ADMIN)
public class BFArcadeCommand extends CustomCommand {

	public BFArcadeCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void open() {
		new ArcadeMachineMenu().open(player());
	}

}
