package gg.projecteden.nexus.features.store.perks.workbenches;

import gg.projecteden.nexus.features.workbenches.DyeStation;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

import static gg.projecteden.nexus.features.store.perks.workbenches._WorkbenchCommand.PERMISSION;

@Aliases("dye")
@Permission(PERMISSION)
public class DyeStationCommand extends _WorkbenchCommand {
	public DyeStationCommand(CommandEvent event) {
		super(event);
	}

	@Override
	protected Workbench getType() {
		return Workbench.DYE_STATION;
	}

	@Path
	@Override
	@Description("Open a dye station")
	void run() {
		DyeStation.open(player());
	}

	@Path("cheat")
	@Permission(Group.STAFF)
	@Description("Open a dye station that doesnt require a magic dye")
	void openCheat() {
		DyeStation.openCheat(player());
	}
}
