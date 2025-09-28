package gg.projecteden.nexus.features.resourcepack.decoration.types.special.BedAddition;

import gg.projecteden.nexus.features.resourcepack.decoration.DecorationTagType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.IBedAddition;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableFloorThing;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import lombok.Getter;

@Getter
public class DyeableBedAddition extends DyeableFloorThing implements IBedAddition {
	private final boolean wide;
	private final AdditionType additionType;

	public DyeableBedAddition(String name, ItemModelType itemModelType, AdditionType additionType, ColorableType colorableType) {
		this(name, itemModelType, additionType, false, colorableType);
	}

	public DyeableBedAddition(String name, ItemModelType itemModelType, AdditionType additionType, boolean wide, ColorableType colorableType) {
		super(true, name, itemModelType, colorableType);

		this.wide = wide;
		this.additionType = additionType;

		DecorationTagType.setLore("&3Can only be placed on a bed", this);
	}

	@Override
	public String getPlacementError() {
		return IBedAddition._getPlacementError();
	}


}
