package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationSnap;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Colorable;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import lombok.Getter;

@Getter
public class Furniture extends Dyeable implements Colorable {

	public Furniture(boolean multiblock, String name, CustomMaterial customMaterial, PlacementType placementType, CustomHitbox hitbox) {
		super(multiblock, name, customMaterial, ColorableType.STAIN, hitbox);

		this.disabledPlacements = placementType.getDisabledPlacements();
		this.rotationSnap = RotationSnap.DEGREE_90;
	}
}
