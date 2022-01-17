package gg.projecteden.nexus.features.listeners;

import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemSetting;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class ItemSettingsListener implements Listener {

	@EventHandler
	public void on(PlayerDropItemEvent event) {
		final ItemStack item = event.getItemDrop().getItemStack();
		if (isNullOrAir(item))
			return;

		if (new ItemBuilder(item).is(ItemSetting.DROPPABLE))
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void on(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof ItemFrame))
			return;

		final EquipmentSlot hand = event.getHand();
		final ItemStack item = event.getPlayer().getInventory().getItem(hand);

		if (isNullOrAir(item))
			return;

		if (new ItemBuilder(item).is(ItemSetting.FRAMEABLE))
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void on(BlockPlaceEvent event) {
		final ItemStack item = event.getItemInHand();
		if (isNullOrAir(item))
			return;

		if (new ItemBuilder(item).is(ItemSetting.PLACEABLE))
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void on(InventoryClickEvent event) {
		final Inventory inventory = event.getClickedInventory();
		if (inventory == null || inventory.getType() == InventoryType.PLAYER)
			return;

		final ItemStack item = event.getWhoClicked().getItemOnCursor();
		if (isNullOrAir(item))
			return;

		if (new ItemBuilder(item).is(ItemSetting.STORABLE))
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void on(InventoryPickupItemEvent event) {
		final ItemStack item = event.getItem().getItemStack();
		if (isNullOrAir(item))
			return;

		if (new ItemBuilder(item).is(ItemSetting.STORABLE))
			return;

		event.setCancelled(true);
	}

}
