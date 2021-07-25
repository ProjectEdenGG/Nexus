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
	COMMON(ColorType.LIGHT_GREEN.getChatColor(),	 0, 5),
	UNCOMMON(ColorType.GREEN.getChatColor(), 		6, 11),
	RARE(ColorType.PINK.getChatColor(),				12, 19),
	EXOTIC(ColorType.PURPLE.getChatColor(),			20, 29),
	EPIC(ColorType.YELLOW.getChatColor(),			30, 39),
	// Uncraftable
	LEGENDARY(ColorType.ORANGE.getChatColor(), false, 40, 49),
	MYTHIC(ColorType.CYAN.getChatColor(), false, 50, 55),
	ARTIFACT(ColorType.LIGHT_RED.getChatColor(), false, 55, 60),
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

	Rarity(ChatColor chatColor, boolean craftable, int min, int max) {
		this.chatColor = chatColor;
		this.craftable = craftable;
		this.min = min;
		this.max = max;
	}

	Rarity(ChatColor chatColor) {
		this.chatColor = chatColor;
		this.craftable = false;
		this.min = null;
		this.max = null;
	}

	public static Rarity of(ItemStack itemStack, Condition condition) {
		return of(itemStack, condition, null);
	}

	public static Rarity of(ItemStack itemStack, Condition condition, Player debugger) {
		if (!ItemTagsUtils.isArmor(itemStack) && !ItemTagsUtils.isTool(itemStack))
			return null;

		if (ItemTagsUtils.isMythicMobsItem(itemStack))
			return null;

		Integer val_material = getMaterialVal(itemStack);
		ItemTags.debug(debugger, "  Material val: " + val_material);

		int val_enchants = getEnchantsVal(itemStack);
		ItemTags.debug(debugger, "  Vanilla enchants val: " + val_enchants);

		int val_customEnchants = getCustomEnchantsVal(itemStack);
		ItemTags.debug(debugger, "  Custom enchants val: " + val_customEnchants);

		if (val_material == null)
			return null;

		int sum = val_material + val_enchants + val_customEnchants;
		ItemTags.debug(debugger, "  Sum: " + sum);

		Rarity rarity;
		if (val_customEnchants > 0)
			rarity = getRarity(sum, false, LEGENDARY, condition, debugger);
		else
			rarity = getRarity(sum, true, COMMON, condition, debugger);

		boolean isCraftable = rarity.isCraftable();
		ItemTags.debug(debugger, "  Is craftable: " + isCraftable);

		return rarity;
	}

	private static Rarity getRarity(int sum, boolean isCraftable, Rarity minimum, Condition condition, Player debugger) {
		if (condition != null && condition != Condition.PRISTINE) {
			sum -= (Condition.values().length - (condition.ordinal() + 1)) * 10;
			ItemTags.debug(debugger, "  Is not pristine, new sum: " + sum);
		}

		Rarity result = null;
		for (Rarity _rarity : Rarity.values()) {
			if (_rarity.getMin() == null || _rarity.getMax() == null)
				continue;

			if (_rarity.isCraftable() != isCraftable)
				continue;

			if (_rarity.getMax() < minimum.getMax())
				continue;

			int min = _rarity.getMin();
			int max = _rarity.getMax();

			if (sum >= min && sum <= max)
				result = _rarity;
		}

		if (result == null) {
			if (minimum != null)
				result = minimum;
			else
				result = COMMON;
		}

		return result;
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

		ItemMeta meta = itemStack.getItemMeta();
		List<String> lore = meta.getLore();
		if (!Utils.isNullOrEmpty(lore)) {
			for (String line : lore) {
				String enchant = StringUtils.stripColor(line)
					.replaceAll("[0-9]+", "") // Custom Enchants bug
					.replaceAll(" [IVXLC]+", "")
					.trim();
				int val = ItemTags.getCustomEnchantVal(enchant);
				result += val;
			}
		}

		return result;
	}
}
