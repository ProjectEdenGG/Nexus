package me.pugabyte.bncore.features.warps.commands;

import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.warps.WarpType;

@Aliases("staffwarp")
@Permission("group.staff")
public class StaffWarpsCommand extends _WarpCommand {

	public StaffWarpsCommand(CommandEvent event) {
		super(event);
	}

	@Override
	WarpType getWarpType() {
		return WarpType.STAFF;
	}

}
