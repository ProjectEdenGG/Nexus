package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.framework.commandsv2.Commands;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.dumpster.Dumpster;
import gg.projecteden.nexus.models.dumpster.DumpsterService;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static gg.projecteden.nexus.features.menus.MenuUtils.handleException;

@HideFromWiki // TODO
@NoArgsConstructor
@Permission(Group.STAFF)
public class DumpsterDivingCommand extends CustomCommand implements Listener {
	private final DumpsterService service = new DumpsterService();
	private final Dumpster dumpster = service.get0();

	public DumpsterDivingCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	void run() {
		new DumpsterProvider().open(player());
	}

	@Path("debug")
	void debug() {
		send(dumpster.toString());
	}

	@Path("test add <material> [amount]")
	void addTest(Material material, @Optional("1") int amount) {
		dumpster.add(new ItemStack(material, amount));
		service.save(dumpster);
		send("Saved");
	}

	@Path("clear")
	void clear() {
		service.clearCache();
		service.deleteAll();
		service.clearCache();
		send("Deleted all dumpster items");
	}

	@NoArgsConstructor
	@Title("&2Dumpster")
	private static class DumpsterProvider extends InventoryProvider {
		private final DumpsterService service = new DumpsterService();
		private final Dumpster dumpster = service.get0();
		private final String PREFIX = Commands.getPrefix(DumpsterDivingCommand.class);

		public void open(Player viewer, int page) {
			if (dumpster.getItems().size() == 0)
				throw new InvalidInputException("Dumpster is empty");

			super.open(viewer, page);
		}

		@Override
		public void init() {
			addCloseItem();

			contents.set(0, 8, ClickableItem.of(Material.IRON_SHOVEL, "Refresh", e -> {
				try {
					open(viewer);
				} catch (Exception ex) {
					handleException(viewer, PREFIX, ex);
				}
			}));

			List<ItemStack> items = new ArrayList<>(dumpster.getItems()).subList(0, Math.min(dumpster.getItems().size(), 5 * 9));
			Collections.shuffle(items);

			for (int row = 1; row <= 5; row++) {
				for (int column = 0; column <= 8; column++) {
					ItemStack item = items.remove(0);
					contents.set(row, column, ClickableItem.of(item, e -> {
						try {
							contents.set(e.getSlot(), ClickableItem.NONE);
							dumpster.getItems().remove(item);
							service.save(dumpster);
							PlayerUtils.giveItem(viewer, item);
						} catch (Exception ex) {
							handleException(viewer, PREFIX, ex);
						}
					}));

					if (items.size() == 0)
						return;
				}
			}
		}

	}

}
