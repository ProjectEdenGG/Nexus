package gg.projecteden.nexus.features.warps.commands;

import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.warps.WarpType;

@Aliases("questwarp")
@Permission(Group.STAFF)
public class QuestWarpsCommand extends _WarpCommand {

	public QuestWarpsCommand(CommandEvent event) {
		super(event);
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.QUEST;
	}

}
