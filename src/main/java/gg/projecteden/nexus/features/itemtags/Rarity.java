package gg.projecteden.nexus.features.itemtags;

import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.StringUtils.Gradient;
import lombok.Getter;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public enum Rarity implements ITag {
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
	@Getter
	private final String tag;

	public static final String NBT_KEY = "ItemTag.RARITY";

	Rarity(ChatColor chatColor, Integer min, Integer max) {
		this(Collections.singletonList(chatColor), true, min, max);
	}

	Rarity(List<ChatColor> chatColors, boolean craftable, Integer min, Integer max) {
		this.chatColors = new ArrayList<>(chatColors);
		this.craftable = craftable;
		this.min = min;
		this.max = max;
		this.tag = rawGetTag();
	}

	Rarity(List<ChatColor> chatColors) {
		this(chatColors, false, null, null);
	}

	public static Rarity of(ItemStack itemStack) {
		return of(itemStack, Condition.of(itemStack), null);
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

		if (itemStack.hasItemMeta()) {
			ItemTags.debug(debugger, "  &3Vanilla Enchants:");
			args.setVanillaEnchantsSum(getEnchantsVal(itemStack, args, debugger));
			ItemTags.debug(debugger, "    &3Sum: &e" + number(args.getVanillaEnchantsSum()));

			ItemTags.debug(debugger, "  &3Custom Enchants:");
			args.setCustomEnchantsSum(getCustomEnchantsVal(itemStack, debugger));
			ItemTags.debug(debugger, "    &3Sum: &e" + number(args.getCustomEnchantsSum()));

			checkCraftableRules(itemStack, args, debugger);
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

	private String rawGetTag() {
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

	private static int getEnchantsVal(ItemStack itemStack, RarityArgs args, Player debugger) {
		int result = 0;

		ItemMeta meta = itemStack.getItemMeta();
		if (meta.hasEnchants()) {
			Map<Enchantment, Integer> enchantMap = meta.getEnchants();
			Set<Enchantment> enchants = enchantMap.keySet();
			for (Enchantment enchant : enchants) {
				int level = enchantMap.get(enchant);
				int enchantVal = ItemTags.getEnchantVal(enchant, level, args);
				result += enchantVal;

				ItemTags.debug(debugger,
					"    &3- " + StringUtils.camelCase(enchant.getKey().getKey()) + " " + level + ": &e" + number(enchantVal));
			}
		}

		return result;
	}

	private static int getCustomEnchantsVal(ItemStack itemStack, Player debugger) {
		int result = 0;

		ItemMeta meta = itemStack.getItemMeta();
		List<String> lore = meta.getLore();
		if (!Nullables.isNullOrEmpty(lore)) {
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
		NBTItem nbtItem = new NBTItem(itemStack);
		if (nbtItem.hasKey(Rarity.NBT_KEY)) {
			return Rarity.valueOf(nbtItem.getString(Rarity.NBT_KEY).toUpperCase());
		}
		return defaultValue;
	}

	private static void checkCraftableRules(ItemStack itemStack, RarityArgs args, Player debugger) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		Set<Enchantment> enchants = itemMeta.getEnchants().keySet();

		// Check compatible enchants
		for (Enchantment enchant : enchants) {
			if (!args.isIncompatibleEnchants() && !enchant.canEnchantItem(itemStack)) {
				args.setIncompatibleEnchants(true);
				break;
			}
		}

		// Check conflicting enchants
		conflicts:
		for (Enchantment enchant : enchants) {
			for (Enchantment _enchant : enchants) {
				if (enchant.equals(_enchant))
					continue;

				if (enchant.conflictsWith(_enchant)) {
					args.setConflictingEnchants(true);
					break conflicts;
				}
			}
		}

		// Check shield banner pattern size
		if (itemStack.getType().equals(Material.SHIELD)) {
			BlockStateMeta blockStateMeta = (BlockStateMeta) itemMeta;
			Banner banner = (Banner) blockStateMeta.getBlockState();
			if (banner.getPatterns().size() > 6)
				args.setUncraftableItem(true);
		}
	}

	public static void setNBT(NBTItem nbtItem, Rarity rarity) {
		nbtItem.setString(NBT_KEY, rarity.name());
		ItemTagsUtils.updateRarity(nbtItem.getItem(), rarity);
	}

	public static final Set<String> ALL_TAGS;
	public static final Set<String> ALL_TAGS_STRIPPED;

	static {
		Set<String> tags = new HashSet<>(Rarity.values().length);
		for (Rarity rarity : Rarity.values())
			tags.add(rarity.getTag());
		ALL_TAGS = Collections.unmodifiableSet(tags);

		Set<String> stripped = new HashSet<>(tags.size());
		for (String tag : tags)
			stripped.add(StringUtils.stripColor(tag));
		ALL_TAGS_STRIPPED = Collections.unmodifiableSet(stripped);
	}

}
