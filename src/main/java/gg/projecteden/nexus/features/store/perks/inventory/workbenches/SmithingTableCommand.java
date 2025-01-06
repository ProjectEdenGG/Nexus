package gg.projecteden.nexus.features.store.perks.inventory.workbenches;

import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.WikiConfig;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Permission(_WorkbenchCommand.PERMISSION)
@WikiConfig(rank = "Store", feature = "Inventory")
public class SmithingTableCommand extends _WorkbenchCommand {

	public SmithingTableCommand(CommandEvent event) {
		super(event);
	}

	@Override
	protected Workbench getType() {
		return Workbench.SMITHING_TABLE;
	}

}
