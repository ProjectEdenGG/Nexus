package gg.projecteden.nexus.features.quests.itemtags;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.Getter;
import lombok.NonNull;
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
	ORDINARY(ColorType.LIGHT_GRAY.getChatColor(),	0, 5),
	COMMON(ColorType.LIGHT_GREEN.getChatColor(),	6, 11),
	UNCOMMON(ColorType.GREEN.getChatColor(), 		12, 19),
	RARE(ColorType.PINK.getChatColor(),				20, 29),
	EPIC(ColorType.YELLOW.getChatColor(),			30, 39),
	// Uncraftable
	EXOTIC(ColorType.PURPLE.getChatColor(),		false, 40, 49),
	LEGENDARY(ColorType.ORANGE.getChatColor(), 	false, 50, 55),
	MYTHIC(ColorType.CYAN.getChatColor(), 		false, 56, 60),

	// Quest Related
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

		boolean isArmor = ItemTagsUtils.isArmor(itemStack);
		String itemType = StringUtils.camelCase(itemStack.getType());
		ItemTags.debug(debugger, isArmor ? "  &3Armor material: &e" + itemType : "  &3Tool material: &e" + itemType);

		int val_material = getMaterialVal(itemStack);
		ItemTags.debug(debugger, "    &3Material sum: &e" + number(val_material));

		int val_vanillaEnchants = 0;
		int val_customEnchants = 0;
		boolean hasAboveVanilla = false;
		if (itemStack.getItemMeta().hasEnchants()) {
			ItemTags.debug(debugger, "  &3Vanilla Enchants:");
			Pair<Integer, Boolean> vanillaEnchantsPair = getEnchantsVal(itemStack, debugger);
			val_vanillaEnchants = vanillaEnchantsPair.getFirst();
			hasAboveVanilla = vanillaEnchantsPair.getSecond();
			ItemTags.debug(debugger, "    &3Sum: &e" + number(val_vanillaEnchants));

			ItemTags.debug(debugger, "  &3Custom Enchants:");
			val_customEnchants = getCustomEnchantsVal(itemStack, debugger);
			ItemTags.debug(debugger, "    &3Sum: &e" + number(val_customEnchants));
		}

		int sum = val_material + val_vanillaEnchants + val_customEnchants;
		boolean isCraftable = val_customEnchants <= 0 && !hasAboveVanilla;

		Rarity rarity;
		if (isCraftable)
			rarity = getRarity(sum, true, ORDINARY, EPIC, condition, debugger);
		else
			rarity = getRarity(sum, false, EXOTIC, MYTHIC, condition, debugger);

		if (condition != null && condition != Condition.PRISTINE) {
			int value = (Condition.values().length * 5) - ((condition.ordinal() + 1) * 5);
			sum -= value;
			ItemTags.debug(debugger, "  &3Condition sum: " + number(value * -1));
		}

		ItemTags.debug(debugger, "  &3Total sum: &a" + sum);
		ItemTags.debug(debugger, "  &3Craftable: &a" + isCraftable);

		return rarity;
	}

	private static Rarity getRarity(int sum, boolean isCraftable, @NonNull Rarity minimum, @NonNull Rarity maximum, Condition condition, Player debugger) {
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
			if (sum >= maximum.getMax())
				result = maximum;
			else if (sum <= maximum.getMin())
				result = minimum;
			else
				result = ORDINARY;
		}

		return result;
	}

	public String getTag() {
		return chatColor == null ? "" : chatColor + "[" + StringUtils.camelCase(this.name()) + "]";
	}

	private static int getMaterialVal(ItemStack itemStack) {
		Integer result;
		if (ItemTagsUtils.isArmor(itemStack))
			result = ItemTags.getArmorMaterialVal(itemStack.getType());
		else
			result = ItemTags.getToolMaterialVal(itemStack.getType());

		if (result == null)
			return 0;

		return result;
	}

	private static Pair<Integer, Boolean> getEnchantsVal(ItemStack itemStack, Player debugger) {
		Pair<Integer, Boolean> result = new Pair<>(0, false);

		ItemMeta meta = itemStack.getItemMeta();
		if (meta.hasEnchants()) {
			Map<Enchantment, Integer> enchantMap = meta.getEnchants();
			Set<Enchantment> enchants = enchantMap.keySet();
			for (Enchantment enchant : enchants) {
				int level = enchantMap.get(enchant);
				Pair<Integer, Boolean> enchantVal = ItemTags.getEnchantVal(enchant, level);

				Integer val = result.getFirst();
				if (val != null) {
					ItemTags.debug(debugger,
						"    &3- " + StringUtils.camelCase(enchant.getKey().getKey()) + " " + level + ": &e" + number(enchantVal.getFirst()));

					val += enchantVal.getFirst();
					result = new Pair<>(val, result.getSecond());
				}

				if (enchantVal.getSecond())
					result = new Pair<>(result.getFirst(), true);
			}
		}

		return result;
	}

	private static int getCustomEnchantsVal(ItemStack itemStack, Player debugger) {
		int result = 0;

		ItemMeta meta = itemStack.getItemMeta();
		List<String> lore = meta.getLore();
		if (!Utils.isNullOrEmpty(lore)) {
			for (String line : lore) {
				String enchant = StringUtils.stripColor(line)
					.replaceAll("[\\d]+", "") // Custom Enchants bug
					.replaceAll(" [IVXLC]+", "")
					.trim();
				Integer val = ItemTags.getCustomEnchantVal(enchant);
				if (val != null) {
					ItemTags.debug(debugger, "    &3- " + enchant + ": &e" + number(val));
					result += val;
				}
			}
		}

		return result;
	}

	public static String number(int value) {
		if (value < 0)
			return "&c" + value;
		else
			return "&e" + value;
	}
}
