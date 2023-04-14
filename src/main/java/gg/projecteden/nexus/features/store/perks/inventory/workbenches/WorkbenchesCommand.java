package gg.projecteden.nexus.features.store.perks.inventory.workbenches;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.store.perks.inventory.workbenches._WorkbenchCommand.Workbench;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.WikiConfig;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;

import static gg.projecteden.nexus.features.store.perks.inventory.workbenches._WorkbenchCommand.PERMISSION;

@Permission(PERMISSION)
@WikiConfig(rank = "Store", feature = "Inventory")
public class WorkbenchesCommand extends CustomCommand {

	public WorkbenchesCommand(CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			if (worldGroup() == WorldGroup.EVENTS)
				permissionError();
	}

	@NoLiterals
	@Description("Open a menu displaying all workbenches")
	void open() {
		new WorkbenchesMenu().open(player());
	}

	@NoLiterals
	@Path("<workbench>")
	@Description("Open a workbench")
	void open(Workbench workbench) {
		workbench.open(player());
	}

	@Rows(1)
	@Title("Workbenches")
	public static class WorkbenchesMenu extends InventoryProvider {

		@Override
		public void init() {
			int index = 0;
			for (Workbench workbench : Workbench.values()) {
				final ItemBuilder builder = new ItemBuilder(workbench.getMaterial())
					.name(StringUtils.camelCase(workbench))
					.modelId(workbench.getModelId());

				contents.set(index++, ClickableItem.of(builder.build(), e -> workbench.open(viewer)));
			}
		}

	}

}
