package me.pugabyte.nexus.features.quests.itemtags;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pugabyte.nexus.utils.ColorType;
import me.pugabyte.nexus.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

@AllArgsConstructor
public enum Condition {
	BROKEN(ColorType.RED.getChatColor(), 76, 100),
	RAGGED(ColorType.LIGHT_RED.getChatColor(), 51, 75),
	WORN(ColorType.CYAN.getChatColor(), 26, 50),
	PRISTINE(ColorType.LIGHT_BLUE.getChatColor(), 0, 25);

	@Getter
	private final ChatColor chatColor;
	@Getter
	private final int min;
	@Getter
	private final int max;

	public static Condition of(ItemStack tool) {
		if (!ItemTagsUtils.isArmor(tool) && !ItemTagsUtils.isTool(tool))
			return null;

		if (ItemTagsUtils.isMythicMobsItem(tool))
			return null;

		ItemMeta meta = tool.getItemMeta();
		if (meta instanceof Damageable) {
			Damageable damageable = (Damageable) meta;
			double damage = damageable.getDamage();
			double maxDurability = tool.getType().getMaxDurability();

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


}
