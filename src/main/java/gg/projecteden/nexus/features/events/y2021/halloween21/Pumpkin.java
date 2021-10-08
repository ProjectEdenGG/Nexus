package gg.projecteden.nexus.features.events.y2021.halloween21;

import gg.projecteden.nexus.features.resourcepack.CustomModel;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;

import static gg.projecteden.nexus.utils.RandomUtils.randomInt;

public class Pumpkin {
	private static final Material MATERIAL = Material.STONE_BUTTON;
	public static final int MIN = 900;
	public static final int MAX = 919;

	private static boolean isOutOfRange(int customModelData) {
		return customModelData < MIN || customModelData > MAX;
	}

	public static CustomModel random() {
		return of(randomCustomModelData());
	}

	public static int randomCustomModelData() {
		return randomInt(MIN, MAX);
	}

	public static CustomModel of(int customModelData) {
		if (isOutOfRange(customModelData))
			return null;

		return CustomModel.of(MATERIAL, customModelData);
	}

	public static ItemBuilder itemOf(int customModelData) {
		if (isOutOfRange(customModelData))
			return null;

		return new ItemBuilder(MATERIAL).customModelData(customModelData);
	}

}
