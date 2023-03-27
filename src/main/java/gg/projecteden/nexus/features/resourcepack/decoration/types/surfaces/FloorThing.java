package gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces;

import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.Shape;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

import java.util.List;

public class FloorThing extends DecorationConfig {
	boolean multiBlock;

	public FloorThing(String name, CustomMaterial material) {
		this(name, material, Shape.NONE);
	}

	public FloorThing(String name, CustomMaterial material, CustomHitbox hitbox) {
		this(name, material, hitbox.getHitboxes());
	}

	public FloorThing(String name, CustomMaterial material, List<Hitbox> hitboxes) {
		this(name, material, hitboxes, false);
	}

	public FloorThing(String name, CustomMaterial material, CustomHitbox hitbox, boolean multiBlock) {
		this(name, material, hitbox.getHitboxes(), multiBlock);
	}

	public FloorThing(String name, CustomMaterial material, List<Hitbox> hitboxes, boolean multiBlock) {
		super(name, material, hitboxes);
		this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.CEILING);
		this.multiBlock = multiBlock;
		if (multiBlock) this.rotatable = false;
	}

	@Override
	public boolean isMultiBlock() {
		return this.multiBlock;
	}
}
