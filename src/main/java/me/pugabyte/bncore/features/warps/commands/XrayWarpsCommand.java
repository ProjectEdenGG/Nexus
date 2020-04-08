package me.pugabyte.bncore.features.warps.commands;

import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.warps.WarpType;

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
