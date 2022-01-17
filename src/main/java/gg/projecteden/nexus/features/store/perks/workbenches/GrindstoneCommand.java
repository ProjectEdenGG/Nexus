package gg.projecteden.nexus.features.store.perks.workbenches;

import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

import static gg.projecteden.nexus.features.store.perks.workbenches.CraftingTableCommand.PERMISSION;

@Permission(PERMISSION)
public class GrindstoneCommand extends _WorkbenchCommand {

	public GrindstoneCommand(CommandEvent event) {
		super(event);
	}

	@Override
	protected Workbench getType() {
		return Workbench.GRINDSTONE;
	}

}
