package gg.projecteden.nexus.features.quests.itemtags;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.StringUtils.Gradient;
import gg.projecteden.nexus.utils.Utils;
import lombok.Getter;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public enum Rarity {
	// @formatter:off
	ORDINARY(ChatColor.of("#9e9e9e"),	 0, 5),
	COMMON(ChatColor.of("#7aff7a"),		 6, 11),
	UNCOMMON(ChatColor.of("#70ffcd"), 	12, 19),
	RARE(ChatColor.of("#7a9dff"),		20, 29),
	EPIC(ChatColor.of("#da55ff"),		30, 39),
	// Uncraftable
	EXOTIC(List.of(ChatColor.of("#ff55ff"), ChatColor.of("#bf47ff"), ChatColor.of("#9747ff")),	false, 40, 49),
	LEGENDARY(List.of(ChatColor.of("#bf47ff"), ChatColor.of("#00aaaa")), 						false, 50, 55),
	MYTHIC(List.of(ChatColor.of("#00aaaa"), ChatColor.of("#00ff91")), 							false, 56, 60),

	// Quest Related
	ARTIFACT(List.of(ChatColor.of("#ff584d"), ChatColor.of("#e39827"))),
	// Code Related
	UNIQUE(List.of(ChatColor.of("#6effaa"), ChatColor.of("#abff4a"))),
	;
	// @formatter:on

	@Getter
	private final List<ChatColor> chatColors;
	@Getter
	private final boolean craftable;
	@Getter
	private final Integer min;
	@Getter
	private final Integer max;

	Rarity(ChatColor chatColor, int min, int max) {
		this.chatColors = Collections.singletonList(chatColor);
		this.craftable = true;
		this.min = min;
		this.max = max;
	}

	Rarity(List<ChatColor> chatColors, boolean craftable, int min, int max) {
		this.chatColors = new ArrayList<>(chatColors);
		this.craftable = craftable;
		this.min = min;
		this.max = max;
	}

	Rarity(List<ChatColor> chatColors) {
		this.chatColors = new ArrayList<>(chatColors);
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

		RarityArgs args = new RarityArgs();
		args.setCondition(condition);

		args.setArmor(ItemTagsUtils.isArmor(itemStack));
		args.setMaterial(itemStack.getType());
		String itemType = StringUtils.camelCase(args.getMaterial());
		ItemTags.debug(debugger, args.isArmor() ? "  &3Armor material: &e" + itemType : "  &3Tool material: &e" + itemType);

		args.setMaterialSum(getMaterialVal(args));
		ItemTags.debug(debugger, "    &3Sum: &e" + number(args.getMaterialSum()));

		if (itemStack.getItemMeta().hasEnchants()) {
			ItemTags.debug(debugger, "  &3Vanilla Enchants:");
			Pair<Integer, Boolean> vanillaEnchantsPair = getEnchantsVal(itemStack, debugger);
			args.setVanillaEnchantsSum(vanillaEnchantsPair.getFirst());
			args.setAboveVanillaEnchants(vanillaEnchantsPair.getSecond());
			ItemTags.debug(debugger, "    &3Sum: &e" + number(args.getVanillaEnchantsSum()));

			ItemTags.debug(debugger, "  &3Custom Enchants:");
			args.setCustomEnchantsSum(getCustomEnchantsVal(itemStack, debugger));
			ItemTags.debug(debugger, "    &3Sum: &e" + number(args.getCustomEnchantsSum()));
		}

		if (condition != null && condition != Condition.PRISTINE) {
			int value = ((Condition.values().length * 5) - ((condition.ordinal() + 1) * 5)) * -1;
			args.setConditionSum(value);
			ItemTags.debug(debugger, "  &3Condition sum: " + number(args.getConditionSum()));
		}

		Rarity rarity;
		if (args.isCraftable())
			rarity = clampRarity(args, ORDINARY, EPIC);
		else
			rarity = clampRarity(args, EXOTIC, MYTHIC);

		rarity = checkUniqueItems(itemStack, args, rarity);
		args.setRarity(rarity);

		ItemTags.debug(debugger, "  &3Total sum: &a" + args.getTotalSum());
		ItemTags.debug(debugger, "  &3Craftable: &a" + args.isCraftable());

		return args.getRarity();
	}

	private static Rarity clampRarity(RarityArgs args, @NonNull Rarity minimum, @NonNull Rarity maximum) {
		Rarity result = null;
		int argSum = args.getTotalSum();

		for (Rarity _rarity : Rarity.values()) {
			if (_rarity.getMin() == null || _rarity.getMax() == null)
				continue;

			if (_rarity.isCraftable() != args.isCraftable())
				continue;

			if (_rarity.getMax() < minimum.getMax())
				continue;

			int min = _rarity.getMin();
			int max = _rarity.getMax();

			if (argSum >= min && argSum <= max)
				result = _rarity;
		}

		if (result == null) {
			if (argSum >= maximum.getMax())
				result = maximum;
			else if (argSum <= maximum.getMin())
				result = minimum;
			else
				result = ORDINARY;
		}

		return result;
	}

	public String getTag() {
		if (chatColors != null && !chatColors.isEmpty()) {
			if (chatColors.size() == 1)
				return chatColors.get(0) + "[" + StringUtils.camelCase(this.name()) + "]";
			return Gradient.of(chatColors).apply("[" + StringUtils.camelCase(this.name()) + "]");
		}

		return "[" + StringUtils.camelCase(this.name()) + "]";
	}

	private static int getMaterialVal(RarityArgs args) {
		Integer result;
		Material material = args.getMaterial();
		if (args.isArmor())
			result = ItemTags.getArmorMaterialVal(material);
		else
			result = ItemTags.getToolMaterialVal(material);

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

	private static Rarity checkUniqueItems(ItemStack itemStack, RarityArgs args, Rarity defaultValue) {
		List<String> lore = itemStack.getItemMeta().getLore();
		if (lore != null && lore.size() != 0) {
			List<String> lore_stripped = lore.stream().map(StringUtils::stripColor).map(String::toLowerCase).toList();

			// Bonemeal boots
			if (lore_stripped.contains("bonemeal boots"))
				return UNIQUE;
		}

		return defaultValue;
	}

}
