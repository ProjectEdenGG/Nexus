package gg.projecteden.nexus.features.quests.itemtags;

import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import static gg.projecteden.nexus.utils.StringUtils.stripColor;

public class ItemTagsUtils {

	public static void debugItem(@NotNull ItemStack itemStack, @NotNull Player debugger) {
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

	public static void updateItem(@NotNull ItemStack itemStack) {
		Condition condition = Condition.of(itemStack);
		Rarity rarity = Rarity.of(itemStack, condition);
		if (condition == null && rarity == null)
			return;

		List<String> origLore = itemStack.getLore();
		final List<String> lore;
		if (origLore == null)
			lore = new ArrayList<>();
		else
			lore = origLore;

		// Clear Tags
		clearTags(lore);

		// Add Tag: Condition
		addTag(lore, condition);

		// Add Tag: Rarity
		addTag(lore, rarity);

		// finalizeItem is not required here as we already know the tags are ordered correctly

		if ((origLore == null || origLore.isEmpty()) && lore.isEmpty())
			return;

		boolean updated = false;
		if (origLore != null) {
			for (String line : lore) {
				if (!origLore.contains(line)) {
					updated = true;
					break;
				}
			}
		} else
			updated = true;

		if (!updated)
			return;

		itemStack.setLore(lore);
	}

	// Grabs all tags and reorders them in the correct order
	public static void finalizeItem(@NotNull List<String> lore) {
		String conditionTag = null;
		String rarityTag = null;

		if (!lore.isEmpty()) {
			Iterator<String> iter = lore.iterator();

			while (iter.hasNext()) {
				String line = iter.next();
				String lineStrip = stripColor(line);

				if (conditionTag == null && Condition.ALL_TAGS_STRIPPED.contains(lineStrip)) {
					conditionTag = line;
					iter.remove();
				} else if (rarityTag == null && Rarity.ALL_TAGS_STRIPPED.contains(lineStrip)) {
					rarityTag = line;
					iter.remove();
				}

				if (conditionTag != null && rarityTag != null)
					break;
			}

			// re-add tags in correct order
			if (conditionTag != null)
				lore.add(conditionTag);

			if (rarityTag != null)
				lore.add(rarityTag);
		}
	}

	public static void clearTags(@NotNull List<String> lore) {
		clearCondition(lore);
		clearRarity(lore);
	}

	public static void clearRarity(@NotNull List<String> lore) {
		if (!lore.isEmpty()) {
			clearTags(lore, Rarity.ALL_TAGS);
		}
	}

	public static void clearCondition(@NotNull List<String> lore) {
		if (!lore.isEmpty()) {
			clearTags(lore, Condition.ALL_TAGS);
		}
	}

	private static void clearTags(@NotNull List<String> lore, @NotNull Collection<String> tags) {
		List<String> loreStrip = new ArrayList<>(lore.size());
		for (String tag : tags)
			loreStrip.add(stripColor(tag));
		lore.removeIf(tag -> loreStrip.contains(stripColor(tag)));
	}

	public static void addTag(@NotNull List<String> lore, @Nullable ITag tag) {
		if (tag != null)
			lore.add(tag.getTag());
	}

	public static void updateRarity(@NotNull ItemStack item, @NotNull Rarity rarity) {
		updateTag(item, rarity, ItemTagsUtils::clearRarity);
	}

	public static void updateCondition(@NotNull ItemStack item, @NotNull Condition condition) {
		updateTag(item, condition, ItemTagsUtils::clearCondition);
	}

	public static void updateTag(@NotNull ItemStack item, @NotNull ITag tag, @NotNull Consumer<List<String>> clear) {
		List<String> lore = item.getLore();
		boolean newLore = lore == null;

		if (newLore)
			lore = new ArrayList<>();
		else
			clear.accept(lore);

		addTag(lore, tag);

		if (!newLore)
			finalizeItem(lore);

		item.setLore(lore);
	}

	public static boolean isArmor(@NotNull ItemStack itemStack) {
		if (itemStack.getType().equals(Material.ELYTRA))
			return true;

		return MaterialTag.ARMOR.isTagged(itemStack);
	}

	public static boolean isTool(@NotNull ItemStack itemStack) {
		Material type = itemStack.getType();
		List<Material> uniqueTools = List.of(Material.SHIELD);

		if (MaterialTag.ARROWS.isTagged(itemStack))
			return false;

		if (uniqueTools.contains(type))
			return true;

		return MaterialTag.WEAPONS.isTagged(itemStack) || MaterialTag.TOOLS.isTagged(itemStack);
	}

	public static boolean isMythicMobsItem(@NotNull ItemStack itemStack) {
		if (true)
			return false; // TODO: remove if mythic mobs is added

		NBTItem nbtItem = new NBTItem(itemStack);
		String nbtString;
		if (nbtItem.hasNBTData()) {
			nbtString = nbtItem.toString();
			return nbtString.contains("MYTHIC_TYPE");
		}

		return false;
	}
}

