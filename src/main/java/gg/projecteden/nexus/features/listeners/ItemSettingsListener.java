package gg.projecteden.nexus.features.listeners;

import gg.projecteden.nexus.features.store.perks.inventory.ItemNameCommand;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemSetting;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ItemSettingsListener implements Listener {

	@EventHandler
	public void onAnvilRenameItem(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player player))
			return;

		Inventory inventory = event.getClickedInventory();
		if (inventory == null || inventory.getType() != InventoryType.ANVIL)
			return;

		if (event.getSlotType() != SlotType.RESULT)
			return;

		ItemStack item = event.getCurrentItem();

		if (Nullables.isNullOrAir(item))
			return;

		if (new ItemBuilder(item).is(ItemSetting.RENAMEABLE))
			return;

		event.setCancelled(true);
		player.closeInventory();
		PlayerUtils.send(player, StringUtils.getPrefix(ItemNameCommand.class) + ItemNameCommand.RENAME_SETTING_ERROR);
	}

	@EventHandler
	public void on(PlayerDropItemEvent event) {
		final ItemStack item = event.getItemDrop().getItemStack();
		if (Nullables.isNullOrAir(item))
			return;

		if (new ItemBuilder(item).is(ItemSetting.DROPPABLE))
			return;

		event.setCancelled(true);
		// TODO: IF YOUR INVENTORY IS FULL, THE ITEM GETS DELETED
	}

	@EventHandler
	public void on(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof ItemFrame))
			return;

		final EquipmentSlot hand = event.getHand();
		final ItemStack item = event.getPlayer().getInventory().getItem(hand);

		if (Nullables.isNullOrAir(item))
			return;

		if (new ItemBuilder(item).is(ItemSetting.FRAMEABLE))
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void on(BlockPlaceEvent event) {
		final ItemStack item = event.getItemInHand();
		if (Nullables.isNullOrAir(item))
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
		if (Nullables.isNullOrAir(item))
			return;

		if (new ItemBuilder(item).is(ItemSetting.STORABLE))
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void on(InventoryPickupItemEvent event) {
		final ItemStack item = event.getItem().getItemStack();
		if (Nullables.isNullOrAir(item))
			return;

		if (new ItemBuilder(item).is(ItemSetting.STORABLE))
			return;

		event.setCancelled(true);
	}

}
