package gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.Shape;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Dyeable;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

import java.util.List;

public class DyeableFloorThing extends Dyeable {
	boolean multiblock = false;

	@Override
	public boolean isMultiBlock() {
		return multiblock;
	}

	public DyeableFloorThing(String name, CustomMaterial material, ColorableType colorableType, String hexOverride) {
		this(name, material, colorableType, hexOverride, Shape.NONE);
	}

	public DyeableFloorThing(String name, CustomMaterial material, ColorableType colorableType) {
		this(name, material, colorableType, Shape.NONE);
	}

	public DyeableFloorThing(String name, CustomMaterial material, ColorableType colorableType, CustomHitbox hitbox) {
		this(name, material, colorableType, null, hitbox);
	}

	public DyeableFloorThing(String name, CustomMaterial material, ColorableType colorableType, String hexOverride, CustomHitbox hitbox) {
		this(name, material, colorableType, hexOverride, hitbox, false);
	}

	public DyeableFloorThing(String name, CustomMaterial material, ColorableType colorableType, CustomHitbox hitbox, boolean multiBlock) {
		this(name, material, colorableType, null, hitbox, multiBlock);
	}

	//

	public DyeableFloorThing(String name, CustomMaterial material, ColorableType colorableType, String hexOverride, CustomHitbox hitbox, boolean multiBlock) {
		super(name, material, colorableType, hexOverride, hitbox.getHitboxes());
		this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.CEILING);
		this.multiblock = multiBlock;
	}
}
