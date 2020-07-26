package me.pugabyte.bncore.features.warps.commands;

import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.warps.WarpType;

@Permission("group.staff")
public class Statue20Command extends _WarpCommand {

	public Statue20Command(CommandEvent event) {
		super(event);
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.STATUE_HUNT20;
	}

}
