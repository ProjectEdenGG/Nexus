package gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces;

import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

import java.util.List;

public class FloorThing extends DecorationConfig {
	boolean multiBlock;

	public FloorThing(String name, CustomMaterial material) {
		this(name, material, HitboxSingle.NONE);
	}

	public FloorThing(String name, CustomMaterial material, CustomHitbox hitbox) {
		this(name, material, hitbox, false);
	}

	public FloorThing(String name, CustomMaterial material, CustomHitbox hitbox, boolean multiBlock) {
		super(name, material, hitbox);

		this.multiBlock = multiBlock;
		this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.CEILING);
		if (multiBlock)
			this.rotatable = false;
	}

	@Override
	public boolean isMultiBlock() {
		return this.multiBlock;
	}
}
