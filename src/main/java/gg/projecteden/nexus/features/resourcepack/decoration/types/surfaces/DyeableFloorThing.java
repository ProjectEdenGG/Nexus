package gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Dyeable;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;

import java.util.List;

public class DyeableFloorThing extends Dyeable {

	public DyeableFloorThing(String name, CustomMaterial material, ColorableType colorableType, String hexOverride) {
		super(name, material, colorableType, hexOverride);
		this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.CEILING);
	}

	public DyeableFloorThing(String name, CustomMaterial material, ColorableType colorableType) {
		super(name, material, colorableType);
		this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.CEILING);
	}

	public DyeableFloorThing(String name, CustomMaterial material, ColorableType colorableType, List<Hitbox> hitboxes) {
		super(name, material, colorableType, hitboxes);
		this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.CEILING);
	}

	public DyeableFloorThing(String name, CustomMaterial material, ColorableType colorableType, CustomHitbox hitbox) {
		super(name, material, colorableType, hitbox.getHitboxes());
		this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.CEILING);
	}

	public DyeableFloorThing(String name, CustomMaterial material, ColorableType colorableType, String hexOverride, List<Hitbox> hitboxes) {
		super(name, material, colorableType, hexOverride, hitboxes);
		this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.CEILING);
	}

	public DyeableFloorThing(String name, CustomMaterial material, ColorableType colorableType, String hexOverride, CustomHitbox hitbox) {
		super(name, material, colorableType, hexOverride, hitbox.getHitboxes());
		this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.CEILING);
	}
}
