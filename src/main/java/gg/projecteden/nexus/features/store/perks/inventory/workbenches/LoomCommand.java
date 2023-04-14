package gg.projecteden.nexus.features.store.perks.inventory.workbenches;

import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.WikiConfig;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;

import static gg.projecteden.nexus.features.store.perks.inventory.workbenches._WorkbenchCommand.PERMISSION;

@Permission(PERMISSION)
@WikiConfig(rank = "Store", feature = "Inventory")
public class LoomCommand extends _WorkbenchCommand {

	public LoomCommand(CommandEvent event) {
		super(event);
	}

	@Override
	protected Workbench getType() {
		return Workbench.LOOM;
	}

}
