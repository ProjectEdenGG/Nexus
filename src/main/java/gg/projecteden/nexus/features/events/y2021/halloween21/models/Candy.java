package gg.projecteden.nexus.features.events.y2021.halloween21.models;

import gg.projecteden.nexus.features.resourcepack.models.ItemModelInstance;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.Nullables;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class Candy {
	private static final Material MATERIAL = Material.COOKIE;
	private static final int MIN = 110;
	private static final int MAX = 178;

	public static boolean isOutOfRange(int modelId) {
		return modelId < MIN || modelId > MAX;
	}

	public static ItemModelInstance random() {
		return ItemModelInstance.of(MATERIAL, /*RandomUtils.randomInt(MIN, MAX)*/ ItemModelType.CANDY_GHOST_CHOCOLATE.getModel());
	}

	public static boolean isCandy(ItemStack item) {
		return !Nullables.isNullOrAir(item) && item.getType() == MATERIAL /* && !isOutOfRange(Model.of(item)) */;
	}

}
