package gg.projecteden.nexus.features.events.y2021.halloween21;

import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import lombok.AllArgsConstructor;
import org.bukkit.Material;

import static gg.projecteden.nexus.utils.RandomUtils.randomInt;

@AllArgsConstructor
public class Candy {
	private static final Material MATERIAL = Material.COOKIE;
	private static final int MIN = 110;
	private static final int MAX = 178;

	public static CustomModel random() {
		return CustomModel.of(MATERIAL, randomInt(MIN, MAX));
	}

}
