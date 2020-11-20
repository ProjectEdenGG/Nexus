package me.pugabyte.nexus.features.warps.commands;

import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.warps.WarpType;

@Aliases("xraywarp")
@Permission("group.staff")
public class XrayWarpsCommand extends _WarpCommand {

	public XrayWarpsCommand(CommandEvent event) {
		super(event);
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.XRAY;
	}
}
