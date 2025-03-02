package gg.projecteden.nexus.features.itemtags;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteItemNBT;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@AllArgsConstructor
public enum Condition implements ITag {
	BROKEN(ChatColor.of("#c92222"), 76, 100),
	RAGGED(ChatColor.of("#f06100"), 51, 75),
	WORN(ChatColor.of("#00aa91"), 26, 50),
	PRISTINE(ChatColor.of("#00fff7"), 0, 25);

	@Getter
	private final ChatColor chatColor;
	@Getter
	private final int min;
	@Getter
	private final int max;
	@Getter
	private final String tag;

	private static final DecimalFormat pf = new DecimalFormat("0.00");
	public static final String NBT_KEY = "ItemTag.CONDITION";

	Condition(ChatColor chatColor, int min, int max) {
		this.chatColor = chatColor;
		this.min = min;
		this.max = max;
		this.tag = (chatColor == null ? "" : chatColor) + "[" + StringUtils.camelCase(this.name()) + "]";
	}

	public static Condition of(ItemStack itemStack) {
		return of(itemStack, null);
	}

	public static Condition of(ItemStack itemStack, Player debugger) {
		if (!ItemTagsUtils.isArmor(itemStack) && !ItemTagsUtils.isTool(itemStack))
			return null;

		if (ItemTagsUtils.isMythicMobsItem(itemStack))
			return null;

		ItemMeta meta = itemStack.getItemMeta();
		if (meta instanceof Damageable damageable) {
			double damage = damageable.getDamage();
			ItemTags.debug(debugger, "  &3Damage: &e" + damage);

			double maxDurability = damageable.hasMaxDamage() ? damageable.getMaxDamage() : itemStack.getType().getMaxDurability();
			ItemTags.debug(debugger, "  &3Max durability: &e" + maxDurability);

			double percentage = (damage / maxDurability) * 100.0;
			String percent = pf.format(percentage);
			ItemTags.debug(debugger, "  &3Broken: &e" + percent + "%");

			for (Condition condition : values()) {
				double min = (condition.getMin() / 100.0) * maxDurability;
				double max = (condition.getMax() / 100.0) * maxDurability;

				if (damage >= min && damage <= max)
					return condition;
			}
		}

		return null;
	}

	public static void setNBT(ItemStack nbtItem, Condition condition) {
		NBT.modify(nbtItem, (Consumer<ReadWriteItemNBT>) nbt -> nbt.setString(NBT_KEY, condition.name()));
		ItemTagsUtils.updateCondition(nbtItem, condition);
	}

	public static ItemStack setDurability(ItemStack item, Condition condition) {
		return ItemBuilder.setDurability(item, RandomUtils.randomInt(condition.getMin(), condition.getMax()));
	}

	public static final Set<String> ALL_TAGS;
	public static final Set<String> ALL_TAGS_STRIPPED;
	static {
		Set<String> tags = new HashSet<>();
		for (Condition condition : Condition.values())
			tags.add(condition.getTag());
		ALL_TAGS = Collections.unmodifiableSet(tags);

		Set<String> stripped = new HashSet<>();
		for (String tag : tags)
			stripped.add(StringUtils.stripColor(tag));
		ALL_TAGS_STRIPPED = Collections.unmodifiableSet(stripped);
	}
}
