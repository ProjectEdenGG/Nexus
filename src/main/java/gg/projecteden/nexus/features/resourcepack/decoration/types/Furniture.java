package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Colorable;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationSnap;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
public class Furniture extends Dyeable implements Colorable {
	private final FurnitureSurface surface;

	public Furniture(boolean multiblock, String name, CustomMaterial customMaterial, FurnitureSurface surface, CustomHitbox hitbox) {
		super(multiblock, name, customMaterial, ColorableType.STAIN, hitbox);

		this.surface = surface;
		this.disabledPlacements = surface.getDisabledPlacements();
		this.rotationSnap = RotationSnap.DEGREE_90;
	}

	@AllArgsConstructor
	public enum FurnitureSurface {
		FLOOR(PlacementType.WALL, PlacementType.CEILING),
		WALL(PlacementType.FLOOR, PlacementType.CEILING),
		CEILING(PlacementType.WALL, PlacementType.FLOOR),
		;

		@Getter
		final List<PlacementType> disabledPlacements;

		FurnitureSurface(PlacementType... types) {
			this.disabledPlacements = List.of(types);
		}
	}
}
