package gg.projecteden.nexus.features.store.perks.inventory.workbenches;

import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.WikiConfig;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

import static gg.projecteden.nexus.features.store.perks.inventory.workbenches._WorkbenchCommand.PERMISSION;

@Aliases({"craft", "workbench"})
@Permission(PERMISSION)
@WikiConfig(rank = "Store", feature = "Inventory")
public class CraftingTableCommand extends _WorkbenchCommand {

	public CraftingTableCommand(CommandEvent event) {
		super(event);
	}

	@Override
	protected Workbench getType() {
		return Workbench.CRAFTING_TABLE;
	}

}
