package gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces;

import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

import java.util.List;

public class CeilingThing extends DecorationConfig {

	public CeilingThing(String name, CustomMaterial material) {
		super(name, material);
		this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.FLOOR);
	}

	public CeilingThing(String name, CustomMaterial material, CustomHitbox hitbox) {
		this(name, material, hitbox.getHitboxes());
	}

	public CeilingThing(String name, CustomMaterial material, List<Hitbox> hitboxes) {
		super(name, material, hitboxes);
		this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.FLOOR);
	}
}
