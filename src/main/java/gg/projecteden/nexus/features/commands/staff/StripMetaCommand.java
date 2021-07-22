package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@NoArgsConstructor
@Permission("group.seniorstaff")
public class StripMetaCommand extends CustomCommand implements Listener {
	private static final String TITLE = StringUtils.colorize("&6Strip Meta");

	public StripMetaCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void menu() {
		Inventory inv = Bukkit.createInventory(null, 54, TITLE);
		player().openInventory(inv);
	}

	@EventHandler
	public void onChestClose(InventoryCloseEvent event) {
		if (event.getInventory().getHolder() != null) return;
		if (!Utils.equalsInvViewTitle(event.getView(), TITLE)) return;

		for (ItemStack item : event.getInventory().getContents()) {
			if (ItemUtils.isNullOrAir(item))
				continue;

			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setDisplayName(null);
			itemMeta.setLore(null);
			item.setItemMeta(itemMeta);

			PlayerUtils.giveItem((Player) event.getPlayer(), item);
		}
	}

}

