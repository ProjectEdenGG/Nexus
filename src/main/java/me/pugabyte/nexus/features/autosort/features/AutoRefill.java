package me.pugabyte.nexus.features.autosort.features;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.models.autosort.AutoSortUser;
import me.pugabyte.nexus.models.tip.Tip.TipType;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.projectiles.ProjectileSource;

import static me.pugabyte.nexus.features.autosort.AutoSort.itemsAreSimilar;

@NoArgsConstructor
public class AutoRefill implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onToolBreak(PlayerItemBreakEvent event) {
		Player player = event.getPlayer();
		PlayerInventory inventory = player.getInventory();
		EquipmentSlot slot = getSlotWithItemStack(inventory, event.getBrokenItem());

		tryRefillStackInHand(player, slot);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		tryRefillStackInHand(player, event.getHand());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onFertilize(BlockFertilizeEvent event) {
		Player player = event.getPlayer();
		if (player == null) return;
		PlayerInventory inventory = player.getInventory();
		EquipmentSlot slot = getSlotWithItemStack(inventory, new ItemStack(Material.BONE_MEAL));
		tryRefillStackInHand(player, slot);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onConsumeItem(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		PlayerInventory inventory = player.getInventory();
		EquipmentSlot slot = getSlotWithItemStack(inventory, event.getItem());
		tryRefillStackInHand(player, slot);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		ProjectileSource source = event.getEntity().getShooter();
		if (!(source instanceof Player player)) return;
		tryRefillStackInHand(player, EquipmentSlot.HAND);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onFeedAnimal(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		tryRefillStackInHand(player, event.getHand());
	}

	public static EquipmentSlot getSlotWithItemStack(PlayerInventory inventory, ItemStack brokenItem) {
		if (itemsAreSimilar(inventory.getItemInMainHand(), brokenItem))
			return EquipmentSlot.HAND;
		if (itemsAreSimilar(inventory.getItemInOffHand(), brokenItem))
			return EquipmentSlot.OFF_HAND;

		return null;
	}

	private void tryRefillStackInHand(Player player, EquipmentSlot slot) {
		if (slot == null) return;

		ItemStack stack;
		int slotIndex;
		PlayerInventory inventory = player.getInventory();
		if (slot == EquipmentSlot.HAND) {
			stack = inventory.getItemInMainHand();
			slotIndex = inventory.getHeldItemSlot();
		} else if (slot == EquipmentSlot.OFF_HAND) {
			stack = inventory.getItemInOffHand();
			slotIndex = 40;
		} else {
			return;
		}

		AutoSortUser autoSortUser = AutoSortUser.of(player);

		if (new MaterialTag(MaterialTag.ALL_AIR, MaterialTag.POTIONS).isTagged(stack.getType()))
			return;
		if (autoSortUser.getAutoRefillExclude().contains(stack.getType()))
			return;

		if (stack.getAmount() != 1)
			return;

		Tasks.wait(2, () -> {
			if (!autoSortUser.isOnline())
				return;

			ItemStack currentStack = inventory.getItem(slotIndex);
			if (currentStack != null) return;

			ItemStack bestMatchStack = null;
			int bestMatchSlot = -1;
			int bestMatchStackSize = Integer.MAX_VALUE;
			for (int i = 0; i < 36; i++) {
				ItemStack itemInSlot = inventory.getItem(i);
				if (itemInSlot == null) continue;
				if (itemsAreSimilar(itemInSlot, stack)) {
					int stackSize = itemInSlot.getAmount();
					if (stackSize < bestMatchStackSize) {
						bestMatchStack = itemInSlot;
						bestMatchSlot = i;
						bestMatchStackSize = stackSize;
					}

					if (bestMatchStackSize == 1) break;
				}
			}

			if (bestMatchStack == null) return;

			inventory.setItem(slotIndex, bestMatchStack);
			inventory.clear(bestMatchSlot);

			AutoSortUser.of(player).tip(TipType.AUTOSORT_REFILL);
		});
	}

}
