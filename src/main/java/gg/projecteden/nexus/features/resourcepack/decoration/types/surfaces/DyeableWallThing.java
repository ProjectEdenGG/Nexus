package gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Dyeable;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;

public class DyeableWallThing extends Dyeable {

	public DyeableWallThing(boolean multiblock, String name, ItemModelType itemModelType, ColorableType colorableType, String hexOverride) {
		super(multiblock, name, itemModelType, colorableType, hexOverride);
		this.disabledPlacements = PlacementType.WALL.getDisabledPlacements();
	}

	public DyeableWallThing(boolean multiblock, String name, ItemModelType itemModelType, ColorableType colorableType) {
		super(multiblock, name, itemModelType, colorableType);
		this.disabledPlacements = PlacementType.WALL.getDisabledPlacements();
	}

	public DyeableWallThing(boolean multiblock, String name, ItemModelType itemModelType, ColorableType colorableType, CustomHitbox hitbox) {
		super(multiblock, name, itemModelType, colorableType, hitbox);
		this.disabledPlacements = PlacementType.WALL.getDisabledPlacements();
	}

}
