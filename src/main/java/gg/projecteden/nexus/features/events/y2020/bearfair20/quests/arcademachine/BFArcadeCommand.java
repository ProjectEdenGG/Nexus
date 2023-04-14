package gg.projecteden.nexus.features.events.y2020.bearfair20.quests.arcademachine;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;

@Disabled
@Permission(Group.ADMIN)
public class BFArcadeCommand extends CustomCommand {

	public BFArcadeCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	void open() {
		new ArcadeMachineMenu().open(player());
	}

}
