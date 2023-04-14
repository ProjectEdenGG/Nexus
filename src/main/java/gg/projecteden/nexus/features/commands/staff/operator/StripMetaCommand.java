package gg.projecteden.nexus.features.commands.staff.operator;

import gg.projecteden.nexus.features.menus.api.TemporaryMenuListener;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
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

	@NoLiterals
	@Description("Strip name and lore from items")
	void menu() {
		new StripMetaMenu(player());
	}

	@Data
	@Title("&6Strip Meta")
	public static class StripMetaMenu implements TemporaryMenuListener {
		private final Player player;

		public StripMetaMenu(Player player) {
			this.player = player;
			open(6);
		}

		@Override
		public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
			for (ItemStack item : contents) {
				if (isNullOrAir(item))
					continue;

				ItemMeta itemMeta = item.getItemMeta();
				itemMeta.displayName(null);
				itemMeta.lore(null);
				item.setItemMeta(itemMeta);

				PlayerUtils.giveItem((Player) event.getPlayer(), item);
			}
		}
	}

}

