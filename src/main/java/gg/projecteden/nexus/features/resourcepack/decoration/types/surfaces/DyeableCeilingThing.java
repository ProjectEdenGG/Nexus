package gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Dyeable;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

import java.util.List;

public class DyeableCeilingThing extends Dyeable {

	public DyeableCeilingThing(boolean multiblock, String name, CustomMaterial material, ColorableType colorableType) {
		this(multiblock, name, material, colorableType, HitboxSingle.NONE);
	}

	public DyeableCeilingThing(boolean multiblock, String name, CustomMaterial material, ColorableType colorableType, CustomHitbox hitbox) {
		this(multiblock, name, material, colorableType, null, hitbox);
	}

	public DyeableCeilingThing(boolean multiblock, String name, CustomMaterial material, ColorableType colorableType, String hexOverride) {
		this(multiblock, name, material, colorableType, hexOverride, HitboxSingle.NONE);
	}

	public DyeableCeilingThing(boolean multiblock, String name, CustomMaterial material, ColorableType colorableType, String hexOverride, CustomHitbox hitbox) {
		super(multiblock, name, material, colorableType, hexOverride, hitbox);
		this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.FLOOR);
	}
}
