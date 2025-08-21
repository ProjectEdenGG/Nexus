package gg.projecteden.nexus.features.equipment.skins;

import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.inventory.ItemStack;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public interface EquipmentSkinType {

	static boolean isTemplate(ItemStack item) {
		if (isNullOrAir(item)) return false;
		String model = new ItemBuilder(item).model();
		if (isNullOrEmpty(model)) return false;
		return model.contains("skin") && model.contains("template");
	}

	ItemStack apply(ItemStack item);

	boolean applies(ItemStack item);

	ItemStack getBig(ItemStack item);

	ItemStack getTemplate();

	static EquipmentSkinType of(ItemStack item) {
		ToolSkin tool = ToolSkin.of(item);
		if (tool != null)
			return tool;

		ArmorSkin armor = ArmorSkin.of(item);
		if (armor != null)
			return armor;

		BackpackSkin backpack = BackpackSkin.of(item);
		if (backpack != null)
			return backpack;

		return null;
	}

	static boolean isApplicable(ItemStack item) {
		if (ToolSkin.DEFAULT.applies(item))
			return true;

		if (ArmorSkin.DEFAULT.applies(item))
			return true;

		if (BackpackSkin.DEFAULT.applies(item))
			return true;

		return false;
	}

}
