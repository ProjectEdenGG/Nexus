package gg.projecteden.nexus.features.events.y2021.halloween21.models;

import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;

public class Pumpkin {
	private static final Material MATERIAL = Material.PAPER;
	public static final int MIN = 22700;
	public static final int MAX = 22719;

	public static boolean isOutOfRange(int modelId) {
		return modelId < MIN || modelId > MAX;
	}

	public static ItemBuilder itemOf(int modelId) {
		if (isOutOfRange(modelId))
			return null;

		return new ItemBuilder(MATERIAL).modelId(modelId);
	}

}
