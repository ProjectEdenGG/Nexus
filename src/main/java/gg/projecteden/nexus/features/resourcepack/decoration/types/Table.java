package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationSnap;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Colorable;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

public class Table extends Dyeable implements Colorable {

	public Table(boolean multiBlock, String name, CustomMaterial material, CustomHitbox hitbox) {
		this(multiBlock, name, material, hitbox, ColorableType.STAIN);
	}

	public Table(boolean multiBlock, String name, CustomMaterial material, CustomHitbox hitbox, ColorableType colorableType) {
		super(multiBlock, name, material, colorableType, hitbox);
		this.rotationSnap = RotationSnap.BOTH;
		this.disabledPlacements = PlacementType.FLOOR.getDisabledPlacements();

		if (this.isMultiBlock())
			this.rotationSnap = RotationSnap.DEGREE_90;
	}
}
