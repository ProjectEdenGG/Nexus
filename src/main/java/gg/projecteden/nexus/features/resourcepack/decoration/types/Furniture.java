package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Colorable;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxShape;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationType;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
public class Furniture extends Dyeable implements Colorable {
	private final FurnitureSurface surface;
	private final boolean disableHitbox;

	public Furniture(String name, CustomMaterial customMaterial, FurnitureSurface surface, HitboxShape size) {
		this(name, customMaterial, surface, size, false);
	}

	public Furniture(String name, CustomMaterial customMaterial, FurnitureSurface surface, HitboxShape shape, boolean disableHitbox) {
		super(name, customMaterial, ColorableType.STAIN);
		this.disableHitbox = disableHitbox;

		this.surface = surface;
		this.disabledPlacements = surface.getDisabledPlacements();

		this.rotationType = RotationType.DEGREE_90;
		if (!disableHitbox)
			this.hitboxes = shape.getHitboxes();
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
