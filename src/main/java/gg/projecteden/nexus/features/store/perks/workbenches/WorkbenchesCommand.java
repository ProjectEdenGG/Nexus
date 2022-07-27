package gg.projecteden.nexus.features.store.perks.workbenches;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.store.perks.workbenches._WorkbenchCommand.Workbench;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;

import static gg.projecteden.nexus.features.store.perks.workbenches.CraftingTableCommand.PERMISSION;

@Permission(PERMISSION)
public class WorkbenchesCommand extends CustomCommand {

	public WorkbenchesCommand(CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			if (worldGroup() == WorldGroup.EVENTS)
				permissionError();
	}

	@Path
	void open() {
		new WorkbenchesMenu().open(player());
	}

	@Path("<workbench>")
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

				contents.set(index++, ClickableItem.of(builder.build(), e -> workbench.open(player)));
			}
		}

	}

}
