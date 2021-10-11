package gg.projecteden.nexus.features.events.y2021.halloween21;

import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;

public class Pumpkin {
	private static final Material MATERIAL = Material.STONE_BUTTON;
	public static final int MIN = 900;
	public static final int MAX = 919;

	public static boolean isOutOfRange(int customModelData) {
		return customModelData < MIN || customModelData > MAX;
	}

	public static ItemBuilder itemOf(int customModelData) {
		if (isOutOfRange(customModelData))
			return null;

		return new ItemBuilder(MATERIAL).customModelData(customModelData);
	}

}
