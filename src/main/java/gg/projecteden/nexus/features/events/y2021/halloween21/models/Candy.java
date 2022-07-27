package gg.projecteden.nexus.features.events.y2021.halloween21.models;

import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.utils.ItemBuilder.ModelId;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.RandomUtils.randomInt;

@AllArgsConstructor
public class Candy {
	private static final Material MATERIAL = Material.COOKIE;
	private static final int MIN = 110;
	private static final int MAX = 178;

	public static boolean isOutOfRange(int modelId) {
		return modelId < MIN || modelId > MAX;
	}

	public static CustomModel random() {
		return CustomModel.of(MATERIAL, randomInt(MIN, MAX));
	}

	public static boolean isCandy(ItemStack item) {
		return !isNullOrAir(item) && item.getType() == MATERIAL && !isOutOfRange(ModelId.of(item));
	}

}
