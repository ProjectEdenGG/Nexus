package gg.projecteden.nexus.features.store.perks.workbenches;

import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

import static gg.projecteden.nexus.features.store.perks.workbenches.CraftingTableCommand.PERMISSION;

@Aliases({"craft", "workbench"})
@Permission(PERMISSION)
public class CraftingTableCommand extends _WorkbenchCommand {

	public CraftingTableCommand(CommandEvent event) {
		super(event);
	}

	@Override
	protected Workbench getType() {
		return Workbench.CRAFTING_TABLE;
	}

}
