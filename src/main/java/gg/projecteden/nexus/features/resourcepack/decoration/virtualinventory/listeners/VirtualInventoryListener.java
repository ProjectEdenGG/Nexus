package gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.listeners;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.VirtualTileManager;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.events.VirtualFurnaceExtractEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.inventories.VirtualFurnace;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.inventories.VirtualInventory;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.tiles.Tile;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.tiles.VirtualChunk;
import gg.projecteden.nexus.utils.NMSUtils;
import gg.projecteden.nexus.utils.Nullables;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb.SpawnReason;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class VirtualInventoryListener implements Listener {
	private final Map<HumanEntity, VirtualInventory> openInventories = new HashMap<>();

	public VirtualInventoryListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	private void on(InventoryClickEvent event) {
		HumanEntity clicker = event.getWhoClicked();
		if (openInventories.containsKey(clicker) && clicker instanceof Player player) {
			VirtualFurnace virtualFurnace = (VirtualFurnace) openInventories.get(clicker);
			int slot = event.getRawSlot();

			// Give XP to player when they extract from the furnace
			if (slot == 2) {
				ItemStack output = virtualFurnace.getOutput();
				if (Nullables.isNotNullOrAir(output)) {
					int exp = Math.round(virtualFurnace.extractExperience());
					VirtualFurnaceExtractEvent extractEvent = new VirtualFurnaceExtractEvent(virtualFurnace, player, output, exp);

					if (extractEvent.callEvent()) {
						NMSUtils.awardExperience(player, player.getLocation(), extractEvent.getExperience(), SpawnReason.FURNACE);

						event.setCurrentItem(extractEvent.getItemStack());
					}
				}
			}
		}
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
		VirtualChunk virtualChunk = VirtualTileManager.getChunk(chunk);
		if (virtualChunk == null)
			return;

		Tile<?> tile = virtualChunk.getTile(block);
		if (tile == null)
			return;

		event.setCancelled(true);
		tile.openInventory(player);
		openInventories.put(player, tile.getVirtualInv());
	}

	@EventHandler
	private void on(InventoryCloseEvent event) {
		VirtualInventory virtualIn = openInventories.remove(event.getPlayer());
		if (virtualIn == null)
			return;

		virtualIn.closeInventory();
	}
}
