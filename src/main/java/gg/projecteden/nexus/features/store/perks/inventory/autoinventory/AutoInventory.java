package gg.projecteden.nexus.features.store.perks.inventory.autoinventory;

import gg.projecteden.api.common.utils.Utils;
import gg.projecteden.nexus.features.store.perks.inventory.autoinventory.tasks.FindChestsThread.DepositRecord;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.autoinventory.AutoInventoryUser;
import gg.projecteden.nexus.models.autoinventory.AutoInventoryUser.AutoSortInventoryType;
import gg.projecteden.nexus.utils.ItemBuilder.ItemSetting;
import gg.projecteden.nexus.utils.ItemBuilder.Model;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Name;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AutoInventory extends Feature {
	public static final String PREFIX = StringUtils.getPrefix("AutoSort");
	public static final String PERMISSION = "store.autoinventory";
	private static final List<String> DISABLED_WORLDS = List.of(WorldGroup.CREATIVE, WorldGroup.MINIGAMES, WorldGroup.STAFF).stream()
			.map(WorldGroup::getWorldNames)
			.reduce(Utils::combine)
			.get();

	@Override
	public void onStart() {
		Tasks.async(() -> gg.projecteden.nexus.utils.Utils.registerListeners(getClass().getPackageName() + ".features"));
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
		if (List.of(Material.BARREL, Material.HOPPER, Material.DROPPER, Material.DISPENSER).contains(block.getType()))
			return true;

		Material blockingMaterial;

		if (MaterialTag.SHULKER_BOXES.isTagged(block.getType())) {
			Directional directional = (Directional) block.getBlockData();
			blockingMaterial = block.getRelative(directional.getFacing()).getType();
		} else
			blockingMaterial = block.getRelative(BlockFace.UP).getType();

		return !blockingMaterial.isOccluding();
	}

	/**
	 * Tests if an inventory can be sorted. Checks if it is a storage inventory and if its title doesn't contain a <code>*</code>.
	 * @param inventory inventory
	 * @param title title of the inventory
	 * @return if the inventory is sortable
	 */
	public static boolean isSortableChestInventory(Player player, Inventory inventory, String title) {
		if (inventory == null || inventory.getHolder() instanceof Player)
			return false;

		AutoInventoryUser user = AutoInventoryUser.of(player);

		AutoSortInventoryType inventoryType = AutoSortInventoryType.of(inventory, title);
		if (inventoryType == null)
			return false;

		if (user.getActiveProfile().getDisabledInventoryTypes().contains(inventoryType))
			return false;

		if (title != null && title.contains("*"))
			return false;

		return true;
	}

	public static DepositRecord depositMatching(AutoInventoryUser autoInventoryUser, Inventory destination, boolean depositHotbar) {
		PlayerInventory source = autoInventoryUser.getOnlinePlayer().getInventory();
		Set<String> eligibleSignatures = new HashSet<>();
		DepositRecord deposits = new DepositRecord();

		for (int i = 0; i < destination.getSize(); i++) {
			ItemStack destinationStack = destination.getItem(i);
			if (destinationStack == null) continue;

			String signature = getSignature(destinationStack);
			eligibleSignatures.add(signature);
		}

		int sourceInventoryStartIndex = depositHotbar ? 0 : 9;
		int sourceInventorySize = Math.min(source.getSize(), 36);

		for (int i = sourceInventoryStartIndex; i < sourceInventorySize; i++) {
			ItemStack sourceStack = source.getItem(i);
			if (Nullables.isNullOrAir(sourceStack))
				continue;

			if (!ItemSetting.AUTODEPOSITABLE.of(sourceStack))
				continue;

			if (autoInventoryUser.getActiveProfile().getAutoDepositExclude().contains(sourceStack.getType()))
				continue;

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

	/**
	 * Returns a string that identifies the provided item
	 * @param stack an item
	 * @return string identifying its data
	 */
	@SuppressWarnings({"removal", "deprecation"})
	private static String getSignature(ItemStack stack) {
		String signature = stack.getType().name();

		ItemMeta itemMeta = stack.getItemMeta();
		if (itemMeta instanceof PotionMeta potionMeta) {
			PotionData potionData = potionMeta.getBasePotionData();
			signature += "." + potionData.getType();
			if (potionData.isExtended()) signature += ".extended";
			if (potionData.isUpgraded()) signature += ".upgraded";
		} else if (itemMeta instanceof FireworkMeta fireworkMeta)
			signature += "." + fireworkMeta.getPower();
		else if (itemMeta instanceof SkullMeta skullMeta && skullMeta.getOwningPlayer() != null)
			signature += "." + (Name.of(skullMeta.getOwningPlayer()) == null);

		var model = Model.of(stack);
		if (model != null)
			signature += "." + model;

		return signature;
	}
}
