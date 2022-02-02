package gg.projecteden.nexus.features.resourcepack.decoration.common;

import gg.projecteden.nexus.utils.ColorType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Material;

public interface Colorable {
	Type getType();

	default boolean isColorable() {
		return !getType().equals(Type.NONE);
	}

	@AllArgsConstructor
	enum Type {
		DYE(ColorType.hexToBukkit("#FF5555")),
		STAIN(ColorType.hexToBukkit("#F4C57A")),
		NONE(null),
		;

		@Getter
		final Color color;
	}

	static Material getTypeMaterial(Type type) {
		if (type.equals(Type.NONE))
			return Material.PAPER;

		return Material.LEATHER_HORSE_ARMOR;
	}
}

