package gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Dyeable;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;

public class DyeableCeilingThing extends Dyeable {

	public DyeableCeilingThing(boolean multiblock, String name, ItemModelType itemModelType, ColorableType colorableType) {
		this(multiblock, name, itemModelType, colorableType, HitboxSingle.NONE);
	}

	public DyeableCeilingThing(boolean multiblock, String name, ItemModelType itemModelType, ColorableType colorableType, CustomHitbox hitbox) {
		this(multiblock, name, itemModelType, colorableType, null, hitbox);
	}

	public DyeableCeilingThing(boolean multiblock, String name, ItemModelType itemModelType, ColorableType colorableType, String hexOverride) {
		this(multiblock, name, itemModelType, colorableType, hexOverride, HitboxSingle.NONE);
	}

	public DyeableCeilingThing(boolean multiblock, String name, ItemModelType itemModelType, ColorableType colorableType, String hexOverride, CustomHitbox hitbox) {
		super(multiblock, name, itemModelType, colorableType, hexOverride, hitbox);
		this.disabledPlacements = PlacementType.CEILING.getDisabledPlacements();
	}
}
