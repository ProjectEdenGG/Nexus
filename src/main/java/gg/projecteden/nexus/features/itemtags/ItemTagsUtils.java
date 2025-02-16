package gg.projecteden.nexus.features.itemtags;

import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.api.common.utils.Nullables;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

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

	public static void update(@NotNull ItemStack itemStack) {
		update(itemStack, null);
	}

	public static void update(@NotNull ItemStack itemStack, @Nullable Player debugger) {
		ItemTags.debug(debugger, "");
		Condition condition = Condition.of(itemStack, debugger);
		Rarity rarity = Rarity.of(itemStack, condition, debugger);
		if (condition == null && rarity == null) {
			ItemTags.debug(debugger, "condition & rarity are null");
			return;
		}

		List<String> origLore = itemStack.getLore();
		final List<String> lore;
		if (origLore == null)
			lore = new ArrayList<>();
		else
			lore = new ArrayList<>(origLore);

		// Clear Tags
		clearTags(lore);

		// Add Tag: Condition
		addTag(lore, condition);

		// Add Tag: Rarity
		addTag(lore, rarity);

		if ((origLore == null || origLore.isEmpty()) && lore.isEmpty()) {
			ItemTags.debug(debugger, "lore: is empty or null");
			return;
		}

		ItemTags.debug(debugger, "old lore: " + origLore);
		ItemTags.debug(debugger, "new lore: " + lore);

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

		if (!updated) {
			ItemTags.debug(debugger, "not updating");
			return;
		}

		itemStack.setLore(lore);
	}

	// Grabs all tags and orders them
	public static void finalizeItem(@NotNull List<String> lore) {
		String conditionTag = null;
		String rarityTag = null;

		if (!lore.isEmpty()) {
			Iterator<String> iter = lore.iterator();

			while (iter.hasNext()) {
				String line = iter.next();
				String lineStrip = StringUtils.stripColor(line);

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

	public static void clearRarity(@NotNull List<String> lore) {
		if (!lore.isEmpty())
			clearTags(lore, Rarity.ALL_TAGS);
	}

	public static void clearCondition(@NotNull List<String> lore) {
		if (!lore.isEmpty())
			clearTags(lore, Condition.ALL_TAGS);
	}

	private static void setLore(ItemStack item, List<String> lore) {
		if (Nullables.isNullOrEmpty(lore))
			lore = null;

		item.setLore(lore);
	}

	public static void clearTags(@NotNull List<String> lore) {
		clearCondition(lore);
		clearRarity(lore);
	}

	public static void clearTags(ItemStack item) {
		if (gg.projecteden.nexus.utils.Nullables.isNullOrAir(item))
			return;

		List<String> lore = item.getLore();
		if (Nullables.isNullOrEmpty(lore))
			return;

		clearCondition(lore);
		clearRarity(lore);
		setLore(item, lore);
	}

	public static void clearRarity(ItemStack item) {
		if (gg.projecteden.nexus.utils.Nullables.isNullOrAir(item))
			return;

		List<String> lore = item.getLore();
		if (Nullables.isNullOrEmpty(lore))
			return;

		clearTags(lore, Rarity.ALL_TAGS);
		setLore(item, lore);
	}

	public static void clearCondition(ItemStack item) {
		if (gg.projecteden.nexus.utils.Nullables.isNullOrAir(item))
			return;

		List<String> lore = item.getLore();
		if (Nullables.isNullOrEmpty(lore))
			return;

		clearTags(lore, Condition.ALL_TAGS);
		setLore(item, lore);
	}

	private static void clearTags(@NotNull List<String> lore, @NotNull Collection<String> tags) {
		List<String> loreStrip = new ArrayList<>(lore.size());
		for (String tag : tags)
			loreStrip.add(StringUtils.stripColor(tag));
		lore.removeIf(tag -> loreStrip.contains(StringUtils.stripColor(tag)));
	}

	public static void addTag(@NotNull List<String> lore, @Nullable ITag tag) {
		if (tag != null)
			lore.add(tag.getTag());
	}

	public static @NotNull ItemStack updateRarity(@NotNull ItemStack item, @NotNull Rarity rarity) {
		return updateTag(item, rarity, ItemTagsUtils::clearRarity);
	}

	public static @NotNull ItemStack updateCondition(@NotNull ItemStack item, @NotNull Condition condition) {
		return updateTag(item, condition, ItemTagsUtils::clearCondition);
	}

	public static @NotNull ItemStack updateTag(@NotNull ItemStack item, @NotNull ITag tag, @NotNull Consumer<List<String>> clear) {
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

		return item;
	}

	public static boolean isTagable(@NotNull ItemStack itemStack) {
		return isArmor(itemStack) || isTool(itemStack);
	}

	public static boolean isArmor(@NotNull ItemStack itemStack) {
		if (itemStack.getType().equals(Material.ELYTRA))
			return true;

		return MaterialTag.ARMOR.isTagged(itemStack);
	}

	private static final Set<Material> ignoreMaterials = new HashSet<>() {{
		addAll(MaterialTag.ARROWS.getValues());
		add(Material.LEAD);
	}};
	private static final Set<Material> uniqueMaterials = Set.of(Material.SHIELD);

	public static boolean isTool(@NotNull ItemStack itemStack) {
		Material type = itemStack.getType();

		if (uniqueMaterials.contains(type))
			return true;

		if (ignoreMaterials.contains(type))
			return false;

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

