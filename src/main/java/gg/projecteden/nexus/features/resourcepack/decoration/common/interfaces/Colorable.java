package gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces;

import gg.projecteden.nexus.utils.ColorType;
import lombok.AllArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Material;

public interface Colorable {
	ColorableType getColorableType();

	default boolean isColorable() {
		return !getColorableType().equals(ColorableType.NONE);
	}

	default Color getColor() {
		return getColorableType().color;
	}

	@AllArgsConstructor
	enum ColorableType {
		DYE(ColorType.hexToBukkit("#FF5555")),
		STAIN(ColorType.hexToBukkit("#F4C57A")),
		NONE(null),
		;

		final Color color;
	}

	static Material getTypeMaterial(ColorableType colorableType) {
		if (colorableType.equals(ColorableType.NONE))
			return Material.PAPER;

		return Material.LEATHER_HORSE_ARMOR;
	}
}

