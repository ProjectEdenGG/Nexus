package gg.projecteden.nexus.features.events.y2025.halloween25;

import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.inventory.ItemStack;

public class Halloween25 {

	public static boolean isCandy(ItemStack item) {
		if (item == null)
			return true;
		String model = new ItemBuilder(item).model();
		if (model == null)
			return false;
		return model.contains("food/candy/wrapped");
	}

}
