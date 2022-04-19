package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.framework.commands.Commands;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
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

import static gg.projecteden.nexus.utils.StringUtils.colorize;

@NoArgsConstructor
@Permission(Group.STAFF)
public class DumpsterDivingCommand extends CustomCommand implements Listener {
	private final DumpsterService service = new DumpsterService();
	private final Dumpster dumpster = service.get0();

	public DumpsterDivingCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		new DumpsterProvider().open(player());
	}

	@Path("debug")
	void debug() {
		send(dumpster.toString());
	}

	@Path("test add <material> [amount]")
	void addTest(Material material, @Arg("1") int amount) {
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
	private static class DumpsterProvider extends MenuUtils implements InventoryProvider {
		private final DumpsterService service = new DumpsterService();
		private final Dumpster dumpster = service.get0();
		private final String PREFIX = Commands.get(DumpsterDivingCommand.class).getPrefix();

		public void open(Player player) {
			if (new DumpsterService().get0().getItems().size() == 0)
				throw new InvalidInputException("Dumpster is empty");

			SmartInventory.builder()
					.provider(new DumpsterProvider())
					.size(6, 9)
					.title(colorize("&2Dumpster"))
					.build()
					.open(player);
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			addCloseItem(contents);

			contents.set(0, 8, ClickableItem.from(nameItem(Material.IRON_SHOVEL, "Refresh"), e -> {
				try {
					open(player);
				} catch (Exception ex) {
					handleException(player, PREFIX, ex);
				}
			}));

			List<ItemStack> items = new ArrayList<>(dumpster.getItems()).subList(0, Math.min(dumpster.getItems().size(), 5 * 9));
			Collections.shuffle(items);

			for (int row = 1; row <= 5; row++) {
				for (int column = 0; column <= 8; column++) {
					ItemStack item = items.remove(0);
					contents.set(row, column, ClickableItem.from(item, e -> {
						try {
							contents.set(e.getSlot(), ClickableItem.NONE);
							dumpster.getItems().remove(item);
							service.save(dumpster);
							PlayerUtils.giveItem(player, item);
						} catch (Exception ex) {
							handleException(player, PREFIX, ex);
						}
					}));

					if (items.size() == 0)
						return;
				}
			}
		}

	}


}
