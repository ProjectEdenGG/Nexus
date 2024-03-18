package gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces;

import gg.projecteden.nexus.utils.ColorType;
import lombok.AllArgsConstructor;
import org.bukkit.Color;

public interface Colorable {
	ColorableType getColorableType();

	default Color getColor() {
		return getColorableType().color;
	}

	@AllArgsConstructor
	enum ColorableType {
		DYE(ColorType.hexToBukkit("#FF5555")),
		STAIN(ColorType.hexToBukkit("#F4C57A")),
		;

		final Color color;
	}
}

