package me.pugabyte.bncore.features.warps.commands;

import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.warps.WarpType;

@Aliases("gilihouse")
@Permission("group.staff")
public class GiliHousesCommand extends _WarpCommand {

	public GiliHousesCommand(CommandEvent event) {
		super(event);
	}

	@Override
	WarpType getWarpType() {
		return WarpType.GILIHOUSE;
	}

}
