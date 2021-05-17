package me.pugabyte.nexus.features.autosort.features;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.autosort.AutoSort;
import me.pugabyte.nexus.features.autosort.AutoSort.FakePlayerInteractEvent;
import me.pugabyte.nexus.features.autosort.AutoSortFeature;
import me.pugabyte.nexus.features.autosort.tasks.FindChestsThread.DepositRecord;
import me.pugabyte.nexus.models.autosort.AutoSortUser;
import org.bukkit.Material;
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

import static me.pugabyte.nexus.features.autosort.AutoSort.PREFIX;
import static me.pugabyte.nexus.utils.PlayerUtils.send;

@NoArgsConstructor
public class AutoDepositQuick implements Listener {


	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onBlockDamage(BlockDamageEvent event) {
		Player player = event.getPlayer();
		AutoSortUser user = AutoSortUser.of(player);
		if (!player.isSneaking()) return;

		if (!user.isFeatureEnabled(AutoSortFeature.QUICK_DEPOSIT))
			return;

		Block clickedBlock = event.getBlock();
		BlockState state = clickedBlock.getState();
		if (!(state instanceof InventoryHolder holder))
			return;

		Inventory inventory = holder.getInventory();
		String name = (state instanceof Nameable nameable) ? nameable.getCustomName() : null;
		AutoSort.isSortableChestInventory(inventory, name);

		PlayerInteractEvent fakeEvent = new FakePlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, player.getInventory().getItemInMainHand(), clickedBlock, BlockFace.EAST);
		if (!fakeEvent.callEvent()) return;

		event.setCancelled(true);

		Material aboveBlockId = clickedBlock.getRelative(BlockFace.UP).getType();
		if (AutoSort.preventsChestOpen(clickedBlock.getType(), aboveBlockId)) {
			send(player, PREFIX + "&cThat chest isn't accessible");
			return;
		}

		DepositRecord deposits = AutoSort.depositMatching(AutoSortUser.of(player), inventory, true);

		if (deposits.isDestinationFull() && deposits.getTotalItems() == 0)
			send(player, PREFIX + "&cThat chest is full");
		else if (deposits.getTotalItems() == 0)
			send(player, PREFIX + "&cNo items deposited &3- none of your inventory items match items in that chest.");
		else
			send(player, PREFIX + "Deposited {0} items", String.valueOf(deposits.getTotalItems()));
	}
}
