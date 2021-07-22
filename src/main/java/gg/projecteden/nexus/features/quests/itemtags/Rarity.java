package gg.projecteden.nexus.features.quests.itemtags;

import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.Set;

public enum Rarity {
	// @formatter:off
	COMMON(ColorType.LIGHT_GREEN.getChatColor(),	 0, 10),
	UNCOMMON(ColorType.GREEN.getChatColor(), 		11, 19),
	RARE(ColorType.PINK.getChatColor(),				20, 24),
	EXOTIC(ColorType.PURPLE.getChatColor(),			25, 29),
	EPIC(ColorType.YELLOW.getChatColor(),			30, 40),
	// Uncraftable
	LEGENDARY(ColorType.ORANGE.getChatColor()),
	MYTHIC(ColorType.CYAN.getChatColor()),
	ARTIFACT(ColorType.LIGHT_RED.getChatColor()),
	// Code Related
	UNIQUE(ColorType.LIGHT_BLUE.getChatColor());
	// @formatter:on

	@Getter
	private final ChatColor chatColor;
	@Getter
	private final boolean craftable;
	@Getter
	private final Integer min;
	@Getter
	private final Integer max;

	Rarity(ChatColor chatColor, int min, int max) {
		this.chatColor = chatColor;
		this.craftable = true;
		this.min = min;
		this.max = max;
	}

	Rarity(ChatColor chatColor) {
		this.chatColor = chatColor;
		this.craftable = false;
		this.min = null;
		this.max = null;
	}

	public static Rarity of(ItemStack itemStack) {
		if (!ItemTagsUtils.isArmor(itemStack) && !ItemTagsUtils.isTool(itemStack))
			return null;

		if (ItemTagsUtils.isMythicMobsItem(itemStack))
			return null;

		Rarity currentRarity = getRarityFromLore(itemStack.getLore());

		// Calculate new rarity, if current rarity is craftable
		if (currentRarity == null || currentRarity.isCraftable()) {
			Integer val_material = getMaterialVal(itemStack);
			int val_enchants = getEnchantsVal(itemStack);
			int val_customEnchants = getCustomEnchantsVal(itemStack);

			if (val_material == null)
				return null;

			int sum = val_material + val_enchants + val_customEnchants;

			if (sum <= COMMON.getMin())
				return COMMON;

			if (sum >= EPIC.getMax())
				return EPIC;

			for (Rarity rarity : Rarity.values()) {
				int min = rarity.getMin();
				int max = rarity.getMax();

				if (sum >= min && sum <= max)
					return rarity;
			}
		}

		// Item is not craftable, return current rarity
		return currentRarity;
	}

	public static Rarity debug(ItemStack itemStack, Player debugger) {
		if (!ItemTagsUtils.isArmor(itemStack) && !ItemTagsUtils.isTool(itemStack))
			return null;

		if (ItemTagsUtils.isMythicMobsItem(itemStack))
			return null;

		Rarity currentRarity = getRarityFromLore(itemStack.getLore());

		// Calculate new rarity, if current rarity is craftable
		if (currentRarity == null || currentRarity.isCraftable()) {
			Integer val_material = getMaterialVal(itemStack);
			debugger.sendMessage("Material Val: " + val_material);
			int val_enchants = getEnchantsVal(itemStack);
			debugger.sendMessage("Vanilla Enchants Val: " + val_enchants);
			int val_customEnchants = getCustomEnchantsVal(itemStack);
			debugger.sendMessage("Custom Enchants Val: " + val_customEnchants);

			if (val_material == null)
				return null;

			int sum = val_material + val_enchants + val_customEnchants;
			debugger.sendMessage("Sum: " + sum);

			if (sum <= COMMON.getMin())
				return COMMON;

			if (sum >= LEGENDARY.getMax())
				return LEGENDARY;

			for (Rarity rarity : Rarity.values()) {
				int min = rarity.getMin();
				int max = rarity.getMax();

				if (sum >= min && sum <= max)
					return rarity;
			}
		}

		// Item is not craftable, return current rarity
		return currentRarity;
	}

	public String getTag() {
		return chatColor == null ? "" : chatColor + "[" + StringUtils.camelCase(this.name()) + "]";
	}


	private static Integer getMaterialVal(ItemStack itemStack) {

		if (ItemTagsUtils.isArmor(itemStack))
			return ItemTags.getArmorMaterialVal(itemStack.getType());

		return ItemTags.getToolMaterialVal(itemStack.getType());
	}

	private static int getEnchantsVal(ItemStack itemStack) {
		int result = 0;

		ItemMeta meta = itemStack.getItemMeta();
		if (meta.hasEnchants()) {
			Map<Enchantment, Integer> enchantMap = meta.getEnchants();
			Set<Enchantment> enchants = enchantMap.keySet();
			for (Enchantment enchant : enchants) {
				int level = enchantMap.get(enchant);
				int val = ItemTags.getEnchantVal(enchant, level);

				result += val;
			}
		}

		return result;
	}

	private static int getCustomEnchantsVal(ItemStack itemStack) {
		int result = 0;

//		ItemMeta meta = itemStack.getItemMeta();
//		if (meta.hasEnchants()) {
//			Map<Enchantment, Integer> enchantMap = meta.getEnchants();
//			Set<Enchantment> enchants = enchantMap.keySet();
//			for (Enchantment enchant : enchants) {
//				int val = ItemTags.getCustomEnchantVal(enchant);
//
//				result += val;
//			}
//		}

		ItemMeta meta = itemStack.getItemMeta();
		List<String> lore = meta.getLore();
		if (!Utils.isNullOrEmpty(lore)) {
			for (String line : lore) {
				String enchant = StringUtils.stripColor(line).replaceAll("[0-9]+", "").trim();
				int val = ItemTags.getCustomEnchantVal(enchant);
				result += val;
			}
		}

		return result;
	}

	private static Rarity getRarityFromLore(List<String> lore) {
		if (lore == null || lore.size() == 0)
			return null;

		for (Rarity rarity : Rarity.values()) {
			String tag = rarity.getTag();
			for (String line : lore) {
				if (tag.equalsIgnoreCase(line)) {
					return rarity;
				}
			}
		}

		return null;
	}
}
