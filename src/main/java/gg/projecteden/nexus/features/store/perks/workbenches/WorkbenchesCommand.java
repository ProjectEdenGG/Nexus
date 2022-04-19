package gg.projecteden.nexus.features.store.perks.workbenches;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.store.perks.workbenches._WorkbenchCommand.Workbench;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.WorldGroup;
import org.bukkit.entity.Player;

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

	public static class WorkbenchesMenu extends InventoryProvider {

		@Override
		public void open(Player player, int page) {
			SmartInventory.builder()
				.title("Workbenches")
				.provider(this)
				.rows(1)
				.build()
				.open(player, page);
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			int index = 0;
			for (Workbench workbench : Workbench.values()) {
				final ItemBuilder builder = new ItemBuilder(workbench.getMaterial())
					.name(StringUtils.camelCase(workbench))
					.customModelData(workbench.getCustomModelData());

				contents.set(index++, ClickableItem.of(builder.build(), e -> workbench.open(player)));
			}
		}

	}

}
