package gg.projecteden.nexus.features.quests.itemtags;

import de.tr7zw.nbtapi.NBTItem;
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

@AllArgsConstructor
public enum Condition {
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
	private static final DecimalFormat pf = new DecimalFormat("0.00");
	public static final String NBT_KEY = "ItemTag.CONDITION";

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

			double maxDurability = itemStack.getType().getMaxDurability();
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

	public String getTag() {
		return (chatColor == null ? "" : chatColor) + "[" + StringUtils.camelCase(this.name()) + "]";
	}

	public static void setNBT(NBTItem nbtItem, Condition condition) {
		nbtItem.setString(NBT_KEY, condition.name());
		ItemTagsUtils.addCondition(nbtItem.getItem(), condition);
		setDurability(nbtItem.getItem(), condition);
	}

	public static void setDurability(ItemStack item, Condition condition) {
		ItemBuilder.setDurability(item, RandomUtils.randomInt(condition.getMin(), condition.getMax()));
	}
}
