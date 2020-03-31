package me.pugabyte.bncore.features.warps.commands;

import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.warps.WarpType;

@Aliases("arena")
@Permission("group.staff")
public class ArenasCommand extends _WarpCommand {

	public ArenasCommand(CommandEvent event) {
		super(event);
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.ARENA;
	}

}
