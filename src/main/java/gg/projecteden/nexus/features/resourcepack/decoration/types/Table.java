package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationSnap;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Colorable;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;

public class Table extends Dyeable implements Colorable {

	public Table(boolean multiBlock, String name, ItemModelType itemModelType, CustomHitbox hitbox) {
		this(multiBlock, name, itemModelType, hitbox, ColorableType.STAIN);
	}

	public Table(boolean multiBlock, String name, ItemModelType itemModelType, CustomHitbox hitbox, ColorableType colorableType) {
		super(multiBlock, name, itemModelType, colorableType, hitbox);
		this.rotationSnap = RotationSnap.BOTH;
		this.disabledPlacements = PlacementType.FLOOR.getDisabledPlacements();

		if (this.isMultiBlock())
			this.rotationSnap = RotationSnap.DEGREE_90;
	}
}
