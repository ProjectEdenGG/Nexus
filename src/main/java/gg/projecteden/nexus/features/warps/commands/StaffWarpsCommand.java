package gg.projecteden.nexus.features.warps.commands;

import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.warps.WarpType;

@Aliases({"staffwarp", "sw"})
@Permission("ladder.noble")
public class StaffWarpsCommand extends _WarpCommand {

	public StaffWarpsCommand(CommandEvent event) {
		super(event);
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.STAFF;
	}

}
