package gg.projecteden.nexus.features.warps.commands;

import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.warps.WarpType;

@Aliases("xraywarp")
@Permission(Group.MODERATOR)
public class XrayWarpsCommand extends _WarpCommand {

	public XrayWarpsCommand(CommandEvent event) {
		super(event);
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.XRAY;
	}
}
