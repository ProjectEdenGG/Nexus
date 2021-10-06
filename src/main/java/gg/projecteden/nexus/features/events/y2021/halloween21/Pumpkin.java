package gg.projecteden.nexus.features.events.y2021.halloween21;

import gg.projecteden.nexus.features.resourcepack.CustomModel;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;

import static gg.projecteden.nexus.utils.RandomUtils.randomInt;

public class Pumpkin {
	private static final Material MATERIAL = Material.STONE_BUTTON;
	private static final int MIN = 900;
	private static final int MAX = 919;

	public static CustomModel random() {
		return of(randomCustomModelData());
	}

	public static int randomCustomModelData() {
		return randomInt(MIN, MAX);
	}

	public static CustomModel of(int customModelData) {
		return CustomModel.of(MATERIAL, customModelData);
	}

	public static ItemBuilder itemOf(int customModelData) {
		return new ItemBuilder(MATERIAL).customModelData(customModelData);
	}

}
