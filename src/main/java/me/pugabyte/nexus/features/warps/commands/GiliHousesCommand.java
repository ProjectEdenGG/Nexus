package me.pugabyte.nexus.features.warps.commands;

import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.warps.WarpType;

@Aliases("gilihouse")
@Permission("group.staff")
public class GiliHousesCommand extends _WarpCommand {

	public GiliHousesCommand(CommandEvent event) {
		super(event);
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.GILIHOUSE;
	}

}
