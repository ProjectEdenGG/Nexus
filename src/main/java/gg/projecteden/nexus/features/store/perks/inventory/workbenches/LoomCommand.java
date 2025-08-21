package gg.projecteden.nexus.features.store.perks.inventory.workbenches;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.WikiConfig;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.PatternType;

import java.util.ArrayList;
import java.util.List;

@Permission(_WorkbenchCommand.PERMISSION)
@WikiConfig(rank = "Store", feature = "Inventory")
public class LoomCommand extends _WorkbenchCommand {

	public LoomCommand(CommandEvent event) {
		super(event);
	}

	@Override
	protected Workbench getType() {
		return Workbench.LOOM;
	}

	@Path("cheat")
	@Permission(Group.STAFF)
	void cheat() {
		new LoomCheatMenu().open(player());
	}

	@Title("Loom")
	public static class LoomCheatMenu extends InventoryProvider {
		private ItemBuilder builder;
		private DyeColor dye;

		@Override
		public void init() {
			addCloseItem();

			if (builder != null)
				contents.set(0, 4, ClickableItem.of(builder, e -> {
					PlayerUtils.giveItem(viewer, builder.build());
					close();
				}));

			List<ClickableItem> items = new ArrayList<>();

			if (builder == null) {
				for (ColorType dye : ColorType.getDyes())
					items.add(ClickableItem.of(new ItemBuilder(dye.getBanner()).name(dye.getDisplayName()).build(), e -> {
						builder = new ItemBuilder(dye.getBanner());
						refresh();
					}));
			} else if (dye == null) {
				for (ColorType color : ColorType.getDyes())
					items.add(ClickableItem.of(new ItemBuilder(color.getDye()).name(color.getDisplayName()).build(), e -> {
						dye = color.getDyeColor();
						refresh();
					}));
			} else {
				var patterns = RegistryAccess.registryAccess().getRegistry(RegistryKey.BANNER_PATTERN);
				for (PatternType pattern : patterns) {
					items.add(ClickableItem.of(new ItemBuilder(builder).pattern(dye, pattern).build(), e -> {
						builder.pattern(dye, pattern);
						dye = null;
						refresh();
					}));
				}
			}

			paginate(items);
		}

	}

}
