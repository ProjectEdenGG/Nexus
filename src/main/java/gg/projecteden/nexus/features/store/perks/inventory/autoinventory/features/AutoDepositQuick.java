package gg.projecteden.nexus.features.store.perks.inventory.autoinventory.features;

import gg.projecteden.nexus.features.listeners.events.fake.FakePlayerInteractEvent;
import gg.projecteden.nexus.features.store.perks.inventory.autoinventory.AutoInventory;
import gg.projecteden.nexus.features.store.perks.inventory.autoinventory.AutoInventoryFeature;
import gg.projecteden.nexus.features.store.perks.inventory.autoinventory.tasks.FindChestsThread.DepositRecord;
import gg.projecteden.nexus.models.autoinventory.AutoInventoryUser;
import lombok.NoArgsConstructor;
import org.bukkit.Nameable;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import static gg.projecteden.nexus.features.store.perks.inventory.autoinventory.AutoInventory.PREFIX;
import static gg.projecteden.nexus.utils.PlayerUtils.send;

@NoArgsConstructor
public class AutoDepositQuick implements Listener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onBlockDamage(BlockDamageEvent event) {
		Player player = event.getPlayer();
		AutoInventoryUser user = AutoInventoryUser.of(player);
		if (!player.isSneaking()) return;

		if (!user.hasFeatureEnabled(AutoInventoryFeature.QUICK_DEPOSIT))
			return;

		Block clickedBlock = event.getBlock();
		BlockState state = clickedBlock.getState();
		if (!(state instanceof InventoryHolder holder))
			return;

		Inventory inventory = holder.getInventory();
		String name = (state instanceof Nameable nameable) ? nameable.getCustomName() : null;
		if (!AutoInventory.isSortableChestInventory(player, inventory, name))
			return;

		PlayerInteractEvent fakeEvent = new FakePlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, player.getInventory().getItemInMainHand(), clickedBlock, BlockFace.EAST);
		if (!fakeEvent.callEvent()) return;

		event.setCancelled(true);

		String materialName = clickedBlock.getType().name().replace('_', ' ').toLowerCase();

		if (!AutoInventory.canOpen(clickedBlock)) {
			send(player, PREFIX + "&cThat " + materialName + " isn't accessible");
			return;
		}

		DepositRecord deposits = AutoInventory.depositMatching(AutoInventoryUser.of(player), inventory, true);

		if (deposits.isDestinationFull() && deposits.getTotalItems() == 0)
			send(player, PREFIX + "&cThat " + materialName + " is full");
		else if (deposits.getTotalItems() == 0)
			send(player, PREFIX + "&cNo items deposited &3- none of your inventory items match items in that " + materialName);
		else
			send(player, PREFIX + "Deposited &e%d &3items", deposits.getTotalItems());
	}

}
