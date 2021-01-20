package me.pugabyte.nexus.features.warps.commands;

import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.warps.WarpType;

@Permission("group.staff")
public class WeeklyWakkaCommand extends _WarpCommand {

	public WeeklyWakkaCommand(CommandEvent event) {
		super(event);
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.WEEKLY_WAKKA;
	}
}
