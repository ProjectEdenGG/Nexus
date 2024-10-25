package gg.projecteden.nexus.features.virtualinventories.listeners;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.virtualinventories.VirtualInventoryUtils.VirtualInventoryHolder;
import gg.projecteden.nexus.features.virtualinventories.events.furnace.VirtualFurnaceExtractEvent;
import gg.projecteden.nexus.features.virtualinventories.events.furnace.VirtualFurnaceTickEvent;
import gg.projecteden.nexus.features.virtualinventories.managers.VirtualSharedInventoryManager;
import gg.projecteden.nexus.features.virtualinventories.models.inventories.impl.VirtualFurnace;
import gg.projecteden.nexus.features.virtualinventories.models.inventories.impl.VirtualPersonalFurnace;
import gg.projecteden.nexus.features.virtualinventories.models.tiles.Tile;
import gg.projecteden.nexus.features.virtualinventories.models.tiles.VirtualChunk;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import gg.projecteden.nexus.utils.Nullables;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.Lightable;
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

public class VirtualInventoryListener implements Listener {

	public VirtualInventoryListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	private void on(InventoryClickEvent event) {
		HumanEntity clicker = event.getWhoClicked();

		if (!(clicker instanceof Player player))
			return;

		if (event.getClickedInventory() == null)
			return;

		if (!(event.getClickedInventory().getHolder() instanceof VirtualInventoryHolder holder))
			return;

		if (!(holder.getVirtualInventory() instanceof VirtualFurnace virtualFurnace))
			return;

		int slot = event.getRawSlot();

		// Give XP to player when they extract from the furnace
		if (slot != 2)
			return;

		ItemStack output = virtualFurnace.getOutput();
		if (!Nullables.isNotNullOrAir(output))
			return;

		int exp = Math.round(virtualFurnace.extractExperience());
		VirtualFurnaceExtractEvent extractEvent = new VirtualFurnaceExtractEvent(virtualFurnace, player, output, exp);

		if (!extractEvent.callEvent()) {
			event.setCancelled(true);
			return;
		}

		NMSUtils.awardExperience(player, player.getLocation(), extractEvent.getExperience(), SpawnReason.FURNACE);
		event.setCurrentItem(extractEvent.getItemStack());
	}

	@EventHandler
	public void on(VirtualFurnaceTickEvent event) {
		if (!(event.getInventory() instanceof VirtualPersonalFurnace furnace))
			return;

		final Location location = furnace.getLocation();
		if (!location.isChunkLoaded())
			return;

		if (!(location.getBlock().getBlockData() instanceof Lightable blockData))
			return;

		if (furnace.getPlayer() == null || !furnace.getPlayer().isOnline())
			return;

		if (!furnace.getPlayer().getWorld().equals(location.getWorld()))
			return;

		blockData.setLit(furnace.isLit());
		furnace.getPlayer().sendBlockChange(location, blockData);
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
	}
}
