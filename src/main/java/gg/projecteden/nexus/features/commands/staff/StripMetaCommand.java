package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.features.commands.StripLogsCommand.StripLogsMenu;
import gg.projecteden.nexus.features.listeners.TemporaryMenuListener;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@NoArgsConstructor
@Permission(Group.SENIOR_STAFF)
public class StripMetaCommand extends CustomCommand implements Listener {

	public StripMetaCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void menu() {
		new StripLogsMenu(player());
	}

	@Data
	@Title("&6Strip Meta")
	public static class StripMenuMenu implements TemporaryMenuListener {
		private final Player player;

		public StripMenuMenu(Player player) {
			this.player = player;
			open(6);
		}

		@Override
		public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
			for (ItemStack item : contents) {
				if (isNullOrAir(item))
					continue;

				ItemMeta itemMeta = item.getItemMeta();
				itemMeta.setDisplayName(null);
				itemMeta.setLore(null);
				item.setItemMeta(itemMeta);

				PlayerUtils.giveItem((Player) event.getPlayer(), item);
			}
		}
	}

}

