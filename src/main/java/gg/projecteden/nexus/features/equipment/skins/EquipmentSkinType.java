package gg.projecteden.nexus.features.equipment.skins;

import org.bukkit.inventory.ItemStack;

public interface EquipmentSkinType {

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
