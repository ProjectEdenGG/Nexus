package me.pugabyte.nexus.features.events.y2020.bearfair20.quests.arcademachine;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

@Permission("group.admin")
public class BFArcadeCommand extends CustomCommand {

	public BFArcadeCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void open() {
		new ArcadeMachineMenu().open(player(), null);
	}

}
