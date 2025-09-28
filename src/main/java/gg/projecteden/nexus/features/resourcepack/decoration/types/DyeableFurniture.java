package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationSnap;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Colorable;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import lombok.Getter;

@Getter
public class DyeableFurniture extends Dyeable implements Colorable {

	public DyeableFurniture(boolean multiblock, String name, ItemModelType itemModelType, PlacementType placementType, CustomHitbox hitbox) {
		super(multiblock, name, itemModelType, ColorableType.STAIN, hitbox);

		this.disabledPlacements = placementType.getDisabledPlacements();
		this.rotationSnap = RotationSnap.DEGREE_90;
	}
}
