package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Colorable;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationType;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.block.BlockFace;

import java.util.List;

@Getter
public class Furniture extends Dyeable implements Colorable {
	private final FurnitureSize size;
	private final FurnitureSurface surface;
	private final boolean disableHitbox;

	public Furniture(String name, CustomMaterial customMaterial, FurnitureSurface surface, FurnitureSize size) {
		this(name, customMaterial, surface, size, false);
	}

	public Furniture(String name, CustomMaterial customMaterial, FurnitureSurface surface, FurnitureSize size, boolean disableHitbox) {
		super(name, customMaterial, ColorableType.STAIN);
		this.disableHitbox = disableHitbox;

		this.size = size;
		this.surface = surface;
		this.disabledPlacements = surface.getDisabledPlacements();

		this.rotationType = RotationType.DEGREE_90;
		if (!disableHitbox)
			this.hitboxes = size.getHitboxes();
	}

	@AllArgsConstructor
	public enum FurnitureSize {
		_1x1(Hitbox.single()),

		_1x2V(List.of(
			Hitbox.origin(),
			Hitbox.offset(BlockFace.UP))
		),

		_1x2H(List.of(
			Hitbox.origin(),
			Hitbox.offset(BlockFace.EAST)
		)),

		_2x2V(List.of(
			Hitbox.origin(),
			Hitbox.offset(BlockFace.UP),
			Hitbox.offset(BlockFace.EAST),
			Hitbox.offset(BlockFace.UP, 1, BlockFace.EAST, 1)
		)),

		_2x3V(List.of(
			Hitbox.origin(),
			Hitbox.offset(BlockFace.EAST, 1),
			Hitbox.offset(BlockFace.UP, 1),
			Hitbox.offset(BlockFace.UP, 1, BlockFace.EAST, 1),
			Hitbox.offset(BlockFace.UP, 2),
			Hitbox.offset(BlockFace.UP, 2, BlockFace.EAST, 1)
		)),
		;

		@Getter
		final List<Hitbox> hitboxes;
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
