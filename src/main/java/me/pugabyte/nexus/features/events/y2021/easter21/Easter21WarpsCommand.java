package me.pugabyte.nexus.features.events.y2021.easter21;

import me.pugabyte.nexus.features.warps.commands._WarpCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.warps.WarpType;

@Permission("group.admin")
public class Easter21WarpsCommand extends _WarpCommand {

	public Easter21WarpsCommand(CommandEvent event) {
		super(event);
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.EASTER21;
	}

}
