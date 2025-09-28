package gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces;

import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang;
import lombok.AllArgsConstructor;
import lombok.Getter;

public interface IBedAddition extends Addition {
	static String _getPlacementError() {
		return DecorationLang.getPREFIX() + "This decoration can only be placed on a bed";
	}

	boolean isWide();

	AdditionType getAdditionType();

	@Getter
	@AllArgsConstructor
	enum AdditionType {
		FRAME(0),
		CANOPY(1),
		;

		private final int modY;
	}
}
