package gg.projecteden.nexus.features.listeners;

import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Nameable;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.ItemStack;

public class RenameChests implements Listener {

	@EventHandler
	public void onClickChest(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		ItemStack item = event.getItem();
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK || block == null || item == null) return;
		if (!(block.getState() instanceof Nameable)) return;
		if (item.getType() != Material.NAME_TAG || !item.getItemMeta().hasDisplayName()) return;

		String name = item.getItemMeta().getDisplayName();
		ConfirmationMenu.builder()
				.title("Rename " + StringUtils.camelCase(block.getType().name()) + "?")
				.onConfirm(e -> {
					BlockState nameable = block.getState();
					((Nameable) nameable).setCustomName(name);
					nameable.update();
					ItemUtils.subtract(event.getPlayer(), item);
				})
				.onCancel(e -> {
					if (block.getState() instanceof BlockInventoryHolder state) {
						event.getPlayer().closeInventory();
						event.getPlayer().openInventory(state.getInventory());
					}
				})
				.open(event.getPlayer());
	}

}
