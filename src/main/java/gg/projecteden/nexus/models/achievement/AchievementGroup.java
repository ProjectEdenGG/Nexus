package gg.projecteden.nexus.models.achievement;

import gg.projecteden.api.common.utils.EnumUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public enum AchievementGroup {
	COMBAT(new ItemStack(Material.DIAMOND_SWORD)),
	SOCIAL(new ItemStack(Material.WRITABLE_BOOK)),
	ECONOMY(new ItemStack(Material.GOLD_INGOT)),
	TRAVEL(new ItemStack(Material.ENDER_PEARL)),
	BIOMES(new ItemStack(Material.SHORT_GRASS)),
	MISC(new ItemStack(Material.POTATO)),

	BEAR_FAIR(new ItemStack(Material.PAINTING));

	@Getter
	private final ItemStack itemStack;

	AchievementGroup(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	@Override
	public String toString() {
		return EnumUtils.prettyName(name());
	}

	public List<Achievement> getAchievements() {
		return Arrays.stream(Achievement.values())
			.filter(ach -> ach.getGroup().equals(this))
			.toList();
	}

}
