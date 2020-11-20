package me.pugabyte.nexus.features.warps.commands;

import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.warps.WarpType;

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
