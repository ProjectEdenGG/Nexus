package gg.projecteden.nexus.features.survival.itemtags;

import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static gg.projecteden.nexus.utils.StringUtils.stripColor;

public class ItemTagsUtils {

	public static void debugItem(ItemStack itemStack, Player debugger) {
		ItemTags.debug(debugger, StringUtils.getPrefix("ItemTags") + " Item debug:");
		ItemTags.debug(debugger, "&3&lCondition:");
		Condition condition = Condition.of(itemStack, debugger);
		if (condition != null) {
			JsonBuilder json = new JsonBuilder("&eTag: &f").group().next(condition.getTag());
			for (Condition value : Condition.values())
				json.hover(value.getTag() + " &f" + value.getMin() + "% - " + value.getMax() + "%");

			ItemTags.debug(debugger, json.loreize(false));
		}

		ItemTags.debug(debugger, "");

		ItemTags.debug(debugger, "&3&lRarity:");
		Rarity rarity = Rarity.of(itemStack, condition, debugger);
		if (rarity != null) {
			JsonBuilder json = new JsonBuilder("&eTag: &f").group().next(rarity.getTag());
			for (Rarity value : Rarity.values()) {
				String minMax = "";
				if (value.getMin() != null && value.getMax() != null)
					minMax = value.getMin() + " - " + value.getMax() + " | ";

				json.hover(value.getTag() + " &f" + minMax + "Craftable: " + value.isCraftable());
			}

			ItemTags.debug(debugger, json.loreize(false));
		}

		ItemTags.debug(debugger, "");
	}

	public static ItemStack updateItem(ItemStack itemStack) {
		Condition condition = Condition.of(itemStack);
		Rarity rarity = Rarity.of(itemStack, condition);
		if (condition == null && rarity == null)
			return itemStack;

		// Clear Tags
		clearTags(itemStack);

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

		List<String> lore = getLore(itemStack);


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

			setLore(itemStack, lore);
		}
		return itemStack;
	}

	public static void clearTags(ItemStack itemStack) {
		clearCondition(itemStack);
		clearRarity(itemStack);

	}

	public static void clearRarity(ItemStack itemStack) {
		List<String> lore = getLore(itemStack);
		if (lore != null && lore.size() > 0) {
			clearTags(lore, Arrays.stream(Rarity.values()).map(Rarity::getTag).toList());
			setLore(itemStack, lore);
		}
	}

	public static void clearCondition(ItemStack itemStack) {
		List<String> lore = getLore(itemStack);
		if (lore != null && lore.size() > 0) {
			clearTags(lore, Arrays.stream(Condition.values()).map(Condition::getTag).toList());
			setLore(itemStack, lore);
		}
	}

	private static void clearTags(List<String> lore, List<String> tags) {
		for (String tag : tags) {
			int ndx = 0;
			for (String line : new ArrayList<>(lore)) {
				String _tag = stripColor(tag);
				String _line = stripColor(line);

				if (_tag.equalsIgnoreCase(_line))
					lore.remove(ndx);
				++ndx;
			}
		}
	}

	public static ItemStack addRarity(ItemStack itemStack, Rarity rarity) {
		return addRarity(itemStack, rarity, false);
	}

	public static ItemStack addRarity(ItemStack itemStack, Rarity rarity, boolean clear) {
		if (clear)
			clearRarity(itemStack);

		if (rarity != null)
			setTag(itemStack, rarity.getTag());

		return itemStack;
	}

	public static ItemStack addCondition(ItemStack itemStack, Condition condition) {
		return addCondition(itemStack, condition, false);
	}

	public static ItemStack addCondition(ItemStack itemStack, Condition condition, boolean clear) {
		if (clear)
			clearCondition(itemStack);

		if (condition != null)
			setTag(itemStack, condition.getTag());

		return itemStack;
	}

	private static void setTag(ItemStack itemStack, String tag) {
		List<String> lore = getLore(itemStack);

		if (lore == null)
			lore = new ArrayList<>();

		lore.add(tag);
		setLore(itemStack, lore);
	}

	public static boolean isArmor(ItemStack itemStack) {
		if (itemStack.getType().equals(Material.ELYTRA))
			return true;

		return MaterialTag.ARMOR.isTagged(itemStack);
	}

	public static boolean isTool(ItemStack itemStack) {
		Material type = itemStack.getType();
		List<Material> uniqueTools = List.of(Material.SHIELD);

		if (MaterialTag.ARROWS.isTagged(itemStack))
			return false;

		if (uniqueTools.contains(type))
			return true;

		return MaterialTag.WEAPONS.isTagged(itemStack) || MaterialTag.TOOLS.isTagged(itemStack);
	}

	public static boolean isMythicMobsItem(ItemStack itemStack) {
		NBTItem nbtItem = new NBTItem(itemStack);
		String nbtString;
		if (nbtItem.hasNBTData()) {
			nbtString = nbtItem.toString();
			return nbtString.contains("MYTHIC_TYPE");
		}

		return false;
	}

	@SuppressWarnings("deprecation")
	private static List<String> getLore(ItemStack itemStack) {
		return itemStack.getLore();
	}

	@SuppressWarnings("deprecation")
	private static void setLore(ItemStack itemStack, List<String> lore) {
		itemStack.setLore(lore);
	}
}

