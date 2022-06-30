package gg.projecteden.nexus.features.warps.commands;

import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.warps.WarpType;

@Aliases("gilihouse")
@Permission(Group.STAFF)
public class GiliHousesCommand extends _WarpCommand {

	public GiliHousesCommand(CommandEvent event) {
		super(event);
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.GILIHOUSE;
	}

}
