package gg.projecteden.nexus.features.virtualinventories.listeners;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.virtualinventories.VirtualInventoryUtils.VirtualInventoryHolder;
import gg.projecteden.nexus.features.virtualinventories.managers.VirtualSharedInventoryManager;
import gg.projecteden.nexus.features.virtualinventories.models.tiles.Tile;
import gg.projecteden.nexus.features.virtualinventories.models.tiles.VirtualChunk;
import gg.projecteden.nexus.models.virtualinventories.VirtualInventoriesConfig;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class VirtualInventoryListener implements Listener {

	public VirtualInventoryListener() {
		Nexus.registerListener(this);
	}

	// TODO: IF PLAYER IS SNEAKING, RETURN --> ALLOW PLACING BLOCKS
	@EventHandler
	private void onClick(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND)
			return;

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		Block block = event.getClickedBlock();
		if (block == null)
			return;

		Player player = event.getPlayer();

		Chunk chunk = block.getChunk();
		VirtualChunk virtualChunk = VirtualSharedInventoryManager.getChunk(chunk);
		if (virtualChunk == null)
			return;

		Tile<?> tile = virtualChunk.getTile(block);
		if (tile == null)
			return;

		event.setCancelled(true);
		tile.openInventory(player);
	}

	@EventHandler
	private void on(InventoryCloseEvent event) {
		if (!(event.getInventory().getHolder() instanceof VirtualInventoryHolder holder))
			return;

		holder.getVirtualInventory().closeInventory();
		VirtualInventoriesConfig.save();
	}
}
