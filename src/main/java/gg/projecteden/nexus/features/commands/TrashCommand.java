package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.menus.api.TemporaryMenuListener;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.dumpster.Dumpster;
import gg.projecteden.nexus.models.dumpster.DumpsterService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemSetting;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@NoArgsConstructor
public class TrashCommand extends CustomCommand implements Listener {

	public TrashCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void trash() {
		new TrashMenu(player());
	}

	@Data
	@Title("&4Trash")
	public static class TrashMenu implements TemporaryMenuListener {
		private final Player player;

		public TrashMenu(Player player) {
			this.player = player;
			open(6);
		}

		@Override
		public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
			DumpsterService service = new DumpsterService();
			Dumpster dumpster = service.get0();

			for (ItemStack item : contents) {
				if (isNullOrAir(item))
					continue;

				if (new ItemBuilder(item).isNot(ItemSetting.TRASHABLE)) {
					PlayerUtils.giveItem((Player) event.getPlayer(), item);
					continue;
				}

				dumpster.add(item);
			}

			service.save(dumpster);
		}
	}

	@Path("<materials...>")
	void trash(@Arg(type = Material.class) List<Material> materials) {
		DumpsterService dumpsterService = new DumpsterService();
		Dumpster dumpster = dumpsterService.get0();

		for (Material material : materials) {
			dumpster.add(inventory().all(material).values());
			inventory().remove(material);
		}

		dumpsterService.save(dumpster);
		send(PREFIX + "Trashed all matching materials");
	}

}
