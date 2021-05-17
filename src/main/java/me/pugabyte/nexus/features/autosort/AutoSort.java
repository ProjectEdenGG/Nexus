package me.pugabyte.nexus.features.autosort;

import lombok.Getter;
import me.pugabyte.nexus.features.autosort.tasks.FindChestsThread.DepositRecord;
import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.models.autosort.AutoSortUser;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static me.pugabyte.nexus.utils.Utils.registerListeners;

public class AutoSort extends Feature {
	public static final String PREFIX = StringUtils.getPrefix("AutoSort");
	@Getter
	private static final String permission = "autosort.use";

	@Override
	public void onStart() {
		registerListeners(getClass().getPackage().getName() + ".features");
	}

	public static class FakePlayerInteractEvent extends PlayerInteractEvent {
		public FakePlayerInteractEvent(Player player, Action action, ItemStack itemInHand, Block clickedBlock, BlockFace blockFace) {
			super(player, action, itemInHand, clickedBlock, blockFace);
		}
	}

	public static boolean itemsAreSimilar(ItemStack a, ItemStack b) {
		if (a.getType() == b.getType())
			return !a.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS) && !a.containsEnchantment(Enchantment.SILK_TOUCH) && !a.containsEnchantment(Enchantment.LOOT_BONUS_MOBS);

		return false;
	}

	public static boolean preventsChestOpen(Material container, Material aboveBlockId) {
		if (container == Material.BARREL) {
			return false;
		}

		if (aboveBlockId == null)
			return false;
		return aboveBlockId.isOccluding();
	}

	public static boolean isSortableChestInventory(Inventory inventory, String name) {
		if (inventory == null) return false;

		InventoryType inventoryType = inventory.getType();
		if (inventoryType != InventoryType.CHEST
				&& inventoryType != InventoryType.ENDER_CHEST
				&& inventoryType != InventoryType.SHULKER_BOX) return false;

		if (name != null && name.contains("*")) return false;

		InventoryHolder holder = inventory.getHolder();
		return holder instanceof Chest
				|| holder instanceof ShulkerBox
				|| holder instanceof DoubleChest
				|| holder instanceof StorageMinecart
				|| holder instanceof Barrel;
	}

	public static DepositRecord depositMatching(AutoSortUser autoSortUser, Inventory destination, boolean depositHotbar) {
		PlayerInventory source = autoSortUser.getOnlinePlayer().getInventory();
		Set<String> eligibleSignatures = new HashSet<>();
		DepositRecord deposits = new DepositRecord();

		for (int i = 0; i < destination.getSize(); i++) {
			ItemStack destinationStack = destination.getItem(i);
			if (destinationStack == null) continue;

			String signature = getSignature(destinationStack);
			eligibleSignatures.add(signature);
		}

		int sourceStartIndex = depositHotbar ? 0 : 9;
		int sourceSize = Math.min(source.getSize(), 36);

		for (int i = sourceStartIndex; i < sourceSize; i++) {
			ItemStack sourceStack = source.getItem(i);
			if (sourceStack == null) continue;

			if (MaterialTag.ALL_AIR.isTagged(sourceStack.getType())) continue;
			if (autoSortUser.getAutoDepositExclude().contains(sourceStack.getType())) continue;

			String signature = getSignature(sourceStack);
			int sourceStackSize = sourceStack.getAmount();
			if (eligibleSignatures.contains(signature)) {
				Map<Integer, ItemStack> notMoved = destination.addItem(sourceStack);
				if (notMoved.isEmpty()) {
					source.clear(i);
					deposits.setTotalItems(deposits.getTotalItems() + sourceStackSize);
				} else {
					int notMovedCount = notMoved.values().iterator().next().getAmount();
					int movedCount = sourceStackSize - notMovedCount;

					if (movedCount == 0)
						eligibleSignatures.remove(signature);
					else {
						int newAmount = sourceStackSize - movedCount;
						sourceStack.setAmount(newAmount);
						deposits.setTotalItems(deposits.getTotalItems() + movedCount);
					}
				}
			}
		}

		if (destination.firstEmpty() == -1)
			deposits.setDestinationFull(true);

		return deposits;
	}

	private static String getSignature(ItemStack stack) {
		String signature = stack.getType().name();
		if (stack.getMaxStackSize() > 1)
			signature += "." + String.valueOf(stack.getData().getData());

		if (stack.getType().toString().toLowerCase().contains("potion")) {
			PotionData potionData = ((PotionMeta) stack.getItemMeta()).getBasePotionData();
			signature += "." + potionData.getType().toString();
			if (potionData.isExtended()) signature += ".extended";
			if (potionData.isUpgraded()) signature += ".upgraded";
		}

		return signature;
	}
}
