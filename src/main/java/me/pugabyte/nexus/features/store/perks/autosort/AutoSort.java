package me.pugabyte.nexus.features.store.perks.autosort;

import eden.utils.Utils;
import me.pugabyte.nexus.features.store.perks.autosort.tasks.FindChestsThread.DepositRecord;
import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.models.autosort.AutoSortUser;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static me.pugabyte.nexus.utils.Utils.registerListeners;

public class AutoSort extends Feature {
	public static final String PREFIX = StringUtils.getPrefix("AutoSort");
	public static final String PERMISSION = "store.autosort";
	private static final List<String> DISABLED_WORLDS = List.of(WorldGroup.CREATIVE, WorldGroup.MINIGAMES, WorldGroup.STAFF).stream()
			.map(WorldGroup::getWorldNames)
			.reduce(Utils::combine)
			.get();

	@Override
	public void onStart() {
		registerListeners(getClass().getPackage().getName() + ".features");
	}

	public static boolean isWorldDisabled(World world) {
		return DISABLED_WORLDS.contains(world.getName());
	}

	/**
	 * Tests if a container block can be opened.
	 * <p>
	 * Examples:
	 * <li>
	 *     <ul>Chest block with Stone above it: <code>false</code></ul>
	 *     <ul>Barrel: <code>true</code></ul>
	 *     <ul>Chest with a slab above it: <code>true</code></ul>
	 * </li>
	 * @param block container block
	 * @return if a player can open the container
	 */
	public static boolean canOpen(Block block) {
		if (block.getType() == Material.BARREL)
			return true;

		Material blockingMaterial;

		if (MaterialTag.SHULKER_BOXES.isTagged(block.getType())) {
			Directional directional = (Directional) block.getBlockData();
			blockingMaterial = block.getRelative(directional.getFacing()).getType();
		} else
			blockingMaterial = block.getRelative(BlockFace.UP).getType();

		return blockingMaterial.isOccluding();
	}

	private static final List<InventoryType> sortableInventories = List.of(
			InventoryType.CHEST,
			InventoryType.ENDER_CHEST,
			InventoryType.BARREL,
			InventoryType.SHULKER_BOX
	);

	/**
	 * Tests if an inventory can be sorted. Checks if it is a storage inventory and if its name doesn't contain a <code>*</code>.
	 * @param inventory inventory
	 * @param name name of the inventory
	 * @return if the inventory is sortable
	 */
	public static boolean isSortableChestInventory(Inventory inventory, String name) {
		if (inventory == null)
			return false;

		if (!sortableInventories.contains(inventory.getType()))
			return false;

		if (name != null && name.contains("*"))
			return false;

		return true;
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

		ItemMeta itemMeta = stack.getItemMeta();
		if (itemMeta instanceof PotionMeta potionMeta) {
			PotionData potionData = potionMeta.getBasePotionData();
			signature += "." + potionData.getType();
			if (potionData.isExtended()) signature += ".extended";
			if (potionData.isUpgraded()) signature += ".upgraded";
		}

		return signature;
	}
}
