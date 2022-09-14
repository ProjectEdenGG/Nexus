package gg.projecteden.nexus.features.resourcepack.decoration.common;

import gg.projecteden.nexus.utils.ColorType;
import lombok.AllArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Material;

public interface Colorable {
	Type getType();

	default boolean isColorable() {
		return !getType().equals(Type.NONE);
	}

	default Color getColor() {
		return getType().color;
	}

	@AllArgsConstructor
	enum Type {
		DYE(ColorType.hexToBukkit("#FF5555")),
		STAIN(ColorType.hexToBukkit("#F4C57A")),
		NONE(null),
		;

		final Color color;
	}

	static Material getTypeMaterial(Type type) {
		if (type.equals(Type.NONE))
			return Material.PAPER;

		return Material.LEATHER_HORSE_ARMOR;
	}
}

