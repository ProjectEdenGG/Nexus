package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Bench;
import gg.projecteden.nexus.features.resourcepack.decoration.common.BlockDecor;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Chair;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.LargeFireplace;
import gg.projecteden.nexus.features.resourcepack.decoration.common.MobPlushie;
import gg.projecteden.nexus.utils.ItemUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public enum Decorations {
	MOB_PLUSHIE_ENDERDRAGON(new MobPlushie("Ender Dragon MobPlushie", -1, 5.0)),
	FIREPLACE_LARGE_DARK(new LargeFireplace("Dark Fireplace", -1)),
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
			if (ItemUtils.isFuzzyMatch(decoration.getItem(), tool))
				return decoration;
		}

		return null;
	}
}
