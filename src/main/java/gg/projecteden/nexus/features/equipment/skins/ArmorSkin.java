package gg.projecteden.nexus.features.equipment.skins;

import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.inventory.ItemStack;

public enum ArmorSkin implements EquipmentSkinType {
	DEFAULT;

	public String getBaseModel() {
		return "skins/" + this.name().toLowerCase();
	}

	@Override
	public ItemStack apply(ItemStack item) {
		return item;
	}

	@Override
	public boolean applies(ItemStack item) {
		return MaterialTag.ARMOR.isTagged(item);
	}

	@Override
	public ItemStack getBig(ItemStack item) {
		return null;
	}

	@Override
	public ItemStack getTemplate() {
		return null;
	}

	public static ArmorSkin of(ItemStack stack) {
		if (stack == null)
			return null;

		String model = ItemBuilder.Model.of(stack);
		if (model == null)
			return null;

		String baseModel = model.toLowerCase().substring(0, model.lastIndexOf('/'));

		for (ArmorSkin skin : values())
			if (skin.getBaseModel().equals(baseModel))
				return skin;

		return null;
	}

}
