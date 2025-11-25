package gg.projecteden.nexus.features.virtualinventories.listeners;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.virtualinventories.VirtualInventoryUtils.VirtualInventoryHolder;
import gg.projecteden.nexus.features.virtualinventories.events.furnace.VirtualFurnaceExtractEvent;
import gg.projecteden.nexus.features.virtualinventories.events.furnace.VirtualFurnaceTickEvent;
import gg.projecteden.nexus.features.virtualinventories.models.inventories.impl.VirtualFurnace;
import gg.projecteden.nexus.features.virtualinventories.models.inventories.impl.VirtualPersonalFurnace;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import org.bukkit.Location;
import org.bukkit.block.data.Lightable;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class VirtualFurnaceListener implements Listener {

	public VirtualFurnaceListener() {
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

		NMSUtils.awardExperience(player.getLocation(), extractEvent.getExperience());
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
}
