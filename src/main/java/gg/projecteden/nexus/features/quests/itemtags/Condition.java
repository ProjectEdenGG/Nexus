package gg.projecteden.nexus.features.quests.itemtags;

import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
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
			ItemTags.debug(debugger, "  &3Broken: &e" + percentage + "%");

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
