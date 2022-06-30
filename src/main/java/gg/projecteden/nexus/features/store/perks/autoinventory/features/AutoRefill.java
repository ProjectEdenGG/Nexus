package gg.projecteden.nexus.features.store.perks.autoinventory.features;

import gg.projecteden.nexus.features.store.perks.autoinventory.AutoInventoryFeature;
import gg.projecteden.nexus.models.autoinventory.AutoInventoryUser;
import gg.projecteden.nexus.models.tip.Tip.TipType;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
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

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@NoArgsConstructor
public class AutoRefill implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onToolBreak(PlayerItemBreakEvent event) {
		Player player = event.getPlayer();
		EquipmentSlot slot = getSlotWithItemStack(player, event.getBrokenItem());
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
		EquipmentSlot slot = getSlotWithItemStack(player, new ItemStack(Material.BONE_MEAL));
		tryRefillStackInHand(player, slot);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onConsumeItem(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		EquipmentSlot slot = getSlotWithItemStack(player, event.getItem());
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

	public static EquipmentSlot getSlotWithItemStack(Player player, ItemStack brokenItem) {
		PlayerInventory inventory = player.getInventory();
		if (itemsAreSimilar(inventory.getItemInMainHand(), brokenItem))
			return EquipmentSlot.HAND;
		if (itemsAreSimilar(inventory.getItemInOffHand(), brokenItem))
			return EquipmentSlot.OFF_HAND;

		return null;
	}

	private void tryRefillStackInHand(Player player, EquipmentSlot slot) {
		if (slot == null)
			return;

		AutoInventoryUser autoInventoryUser = AutoInventoryUser.of(player);
		if (!autoInventoryUser.hasFeatureEnabled(AutoInventoryFeature.REFILL))
			return;

		EquipmentSlot handWithTool = ItemUtils.getHandWithTool(player);
		if (handWithTool == null)
			return;

		PlayerInventory inventory = player.getInventory();
		int slotIndex = handWithTool == EquipmentSlot.OFF_HAND ? 40 : inventory.getHeldItemSlot();
		ItemStack tool = player.getInventory().getItem(handWithTool);
		if (isNullOrAir(tool))
			return;

		ItemStack stack = tool.clone();

		if (stack.getAmount() != 1)
			return;

		if (new MaterialTag(MaterialTag.ALL_AIR, MaterialTag.POTIONS).isTagged(stack.getType()))
			return;
		if (autoInventoryUser.getAutoRefillExclude().contains(stack.getType()))
			return;

		Tasks.wait(2, () -> {
			if (!autoInventoryUser.isOnline())
				return;

			ItemStack currentStack = inventory.getItem(slotIndex);
			if (currentStack != null)
				return;

			ItemStack bestMatchStack = null;
			int bestMatchSlot = -1;
			int bestMatchStackSize = Integer.MAX_VALUE;
			for (int i = 0; i < 36; i++) {
				ItemStack itemInSlot = inventory.getItem(i);
				if (isNullOrAir(itemInSlot))
					continue;

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

			if (bestMatchStack == null)
				return;

			inventory.setItem(slotIndex, bestMatchStack);
			inventory.clear(bestMatchSlot);

			AutoInventoryUser.of(player).tip(TipType.AUTOSORT_REFILL);
		});
	}

	/**
	 * Tests if an item can be replaced by another.
	 * <p>
	 * Currently, this checks if the two items are of the same type, and if b has specific matching enchants.
	 * These enchants are Silk Touch, Fortune, and Looting.
	 * @param a current item
	 * @param b potential replacement item
	 * @return if <code>b</code> is a suitable replacement for <code>a</code>
	 */
	public static boolean itemsAreSimilar(ItemStack a, ItemStack b) {
		if (a.getType() == b.getType())
			return !((a.containsEnchantment(Enchant.SILK_TOUCH) && !b.containsEnchantment(Enchant.SILK_TOUCH))
					|| (a.containsEnchantment(Enchant.FORTUNE) && !b.containsEnchantment(Enchant.FORTUNE))
					|| (a.containsEnchantment(Enchant.LOOTING) && !b.containsEnchantment(Enchant.LOOTING)));

		return false;
	}

}
