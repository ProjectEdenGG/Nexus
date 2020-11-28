package me.pugabyte.nexus.features.listeners;

import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.Nameable;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class RenameChests implements Listener {

	@EventHandler
	public void onClickChest(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (!(event.getClickedBlock().getState() instanceof Nameable)) return;
		if (event.getItem() == null) return;
		ItemStack item = event.getItem();
		if (item.getType() != Material.NAME_TAG) return;
		if (!item.getItemMeta().hasDisplayName()) return;
		String name = item.getItemMeta().getDisplayName();
		MenuUtils.ConfirmationMenu.builder()
				.title("Rename " + StringUtils.camelCase(event.getClickedBlock().getType().name()) + "?")
				.onConfirm(e -> {
					Block block = event.getClickedBlock();
					Nameable nameable = (Nameable) block.getState();
					nameable.setCustomName(name);
					block.getState().update();
				}).open(event.getPlayer());
	}

}
