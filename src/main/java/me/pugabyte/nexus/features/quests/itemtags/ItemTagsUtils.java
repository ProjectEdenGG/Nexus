package me.pugabyte.nexus.features.quests.itemtags;

import de.tr7zw.nbtapi.NBTItem;
import me.pugabyte.nexus.utils.MaterialTag;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.pugabyte.nexus.utils.StringUtils.stripColor;

public class ItemTagsUtils {

	public static void debugItem(ItemStack itemStack, Player debugger) {
		debugger.sendMessage("====");
		debugger.sendMessage("Condition Debug:");
		Condition condition = Condition.debug(itemStack, debugger);
		if (condition != null)
			debugger.sendMessage("Tag: " + condition.getTag());

		debugger.sendMessage("");

		debugger.sendMessage("Rarity Debug:");
		Rarity rarity = Rarity.debug(itemStack, debugger);
		if (rarity != null)
			debugger.sendMessage("Tag: " + rarity.getTag());

		debugger.sendMessage("====");
	}

	public static ItemStack updateItem(ItemStack itemStack) {
		Condition condition = Condition.of(itemStack);
		Rarity rarity = Rarity.of(itemStack);
		if (condition == null && rarity == null)
			return itemStack;

		// Clear Tags
		itemStack = clearTags(itemStack);

		// Add Tag: Condition
		itemStack = addCondition(itemStack, condition);

		// Add Tag: Rarity
		itemStack = addRarity(itemStack, rarity);

		return finalizeItem(itemStack);
	}

	// Grabs all tags and reorders them in the correct order
	public static ItemStack finalizeItem(ItemStack itemStack) {
		String conditionTag = null;
		String rarityTag = null;

		List<String> lore = itemStack.getLore();


		if (lore != null && lore.size() > 0) {
			// Find condition tag
			int ndx = 0;
			for (String line : new ArrayList<>(lore)) {
				if (conditionTag == null) {
					for (Condition condition : Condition.values()) {
						String tag = stripColor(condition.getTag());
						String _line = stripColor(line);

						if (tag.equalsIgnoreCase(_line)) {
							conditionTag = line;
							lore.remove(ndx);
							break;
						}
					}
					++ndx;
				} else
					break;
			}

			// Find rarity tag
			ndx = 0;
			for (String line : new ArrayList<>(lore)) {
				if (rarityTag == null) {
					for (Rarity rarity : Rarity.values()) {
						String tag = stripColor(rarity.getTag());
						String _line = stripColor(line);

						if (tag.equalsIgnoreCase(_line)) {
							rarityTag = line;
							lore.remove(ndx);
							break;
						}
					}
					++ndx;
				} else
					break;
			}

			// re-add tags in correct order
			if (conditionTag != null)
				lore.add(conditionTag);

			if (rarityTag != null)
				lore.add(rarityTag);

			itemStack.setLore(lore);
		}
		return itemStack;
	}

	public static ItemStack clearTags(ItemStack itemStack) {

		clearCondition(itemStack);
		clearRarity(itemStack);

		return itemStack;
	}

	public static ItemStack clearRarity(ItemStack itemStack) {
		List<String> lore = itemStack.getLore();
		if (lore != null && lore.size() > 0) {
			for (Rarity _rarity : Rarity.values()) {
				int ndx = 0;
				for (String line : new ArrayList<>(lore)) {
					String tag = stripColor(_rarity.getTag());
					String _line = stripColor(line);

					if (tag.equalsIgnoreCase(_line))
						lore.remove(ndx);
					++ndx;
				}
			}

			itemStack.setLore(lore);
		}

		return itemStack;
	}

	public static ItemStack clearCondition(ItemStack itemStack) {
		List<String> lore = itemStack.getLore();
		if (lore != null && lore.size() > 0) {
			for (Condition _condition : Condition.values()) {
				int ndx = 0;
				for (String line : new ArrayList<>(lore)) {
					String tag = stripColor(_condition.getTag());
					String _line = stripColor(line);

					if (tag.equalsIgnoreCase(_line))
						lore.remove(ndx);
					++ndx;
				}
			}

			itemStack.setLore(lore);
		}

		return itemStack;
	}

	public static ItemStack addRarity(ItemStack itemStack, Rarity rarity) {
		return addRarity(itemStack, rarity, false);
	}

	public static ItemStack addRarity(ItemStack itemStack, Rarity rarity, boolean clear) {
		// Clear Rarity Tag
		if (clear)
			clearRarity(itemStack);

		if (rarity != null) {
			List<String> lore = itemStack.getLore();
			String rarityTag = rarity.getTag();

			if (lore == null)
				lore = new ArrayList<>();

			lore.add(rarityTag);
			itemStack.setLore(lore);
		}

		return itemStack;
	}

	public static ItemStack addCondition(ItemStack itemStack, Condition condition) {
		return addCondition(itemStack, condition, false);
	}

	public static ItemStack addCondition(ItemStack itemStack, Condition condition, boolean clear) {
		if (clear)
			clearCondition(itemStack);

		if (condition != null) {
			List<String> lore = itemStack.getLore();
			String conditionTag = condition.getTag();

			if (lore == null)
				lore = new ArrayList<>();

			lore.add(conditionTag);
			itemStack.setLore(lore);
		}

		return itemStack;
	}

	public static boolean isArmor(ItemStack itemStack) {
		if (itemStack.getType().equals(Material.ELYTRA))
			return true;

		return MaterialTag.ARMOR.isTagged(itemStack);
	}

	public static boolean isTool(ItemStack itemStack) {
		Material type = itemStack.getType();
		List<Material> uniqueTools = Arrays.asList(Material.SHIELD);

		if (uniqueTools.contains(type))
			return true;

		return MaterialTag.WEAPONS.isTagged(itemStack) || MaterialTag.TOOLS.isTagged(itemStack);
	}

	public static boolean isMythicMobsItem(ItemStack itemStack) {
		NBTItem nbtItem = new NBTItem(itemStack);
		String nbtString;
		if (nbtItem.hasNBTData()) {
			nbtString = nbtItem.toString();
			if (nbtString.contains("MYTHIC_TYPE"))
				return true;
		}

		return false;
	}
}

