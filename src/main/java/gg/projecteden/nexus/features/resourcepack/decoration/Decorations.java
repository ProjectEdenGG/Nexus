package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Bench;
import gg.projecteden.nexus.features.resourcepack.decoration.types.BlockDecor;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Chair;
import gg.projecteden.nexus.utils.ItemBuilder.CustomModelData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public enum Decorations {
//	MOB_PLUSHIE_ENDERDRAGON(new MobPlushie("Ender Dragon MobPlushie", -1, 5.0)),
//	FIREPLACE_LARGE_DARK(new LargeFireplace("Dark Fireplace", -1)),
DYE_STATION(new BlockDecor("Dye Station", 1, Material.CRAFTING_TABLE)),
	CHAIR_WOODEN_BASIC(new Chair("Wooden Chair", 400)),
	CHAIR_WOODEN_CUSHION(new Chair("Cushioned Wooden Chair", 401)),
	BENCH_WOODEN(new Bench("Wooden Bench", 450));

	@Getter
	Decoration decoration;

	public ItemStack getItem() {
		return decoration.getItem().clone();
	}

	public static Decorations of(ItemStack tool) {
		for (Decorations decoration : values()) {
			if (decoration.isFuzzyMatch(tool))
				return decoration;
		}

		return null;
	}

	public boolean isFuzzyMatch(ItemStack item2) {
		ItemStack item1 = this.getItem();

		if (item2 == null)
			return false;

		if (!item1.getType().equals(item2.getType()))
			return false;

		int decorModelData = CustomModelData.of(item1);
		int itemModelData = CustomModelData.of(item2);
		if (decorModelData != itemModelData)
			return false;

		return true;
	}
}
