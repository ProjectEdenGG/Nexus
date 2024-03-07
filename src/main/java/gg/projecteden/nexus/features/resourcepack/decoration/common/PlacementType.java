package gg.projecteden.nexus.features.resourcepack.decoration.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.block.BlockFace;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public enum PlacementType {
	FLOOR(BlockFace.UP),
	WALL(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST),
	CEILING(BlockFace.DOWN),
	;

	PlacementType(BlockFace... blockFaces) {
		this.blockFaces = Arrays.asList(blockFaces);
	}

	@Getter
	final List<BlockFace> blockFaces;

	public List<PlacementType> getDisabledPlacements() {
		return switch (this) {
			case FLOOR -> List.of(PlacementType.WALL, PlacementType.CEILING);
			case WALL -> List.of(PlacementType.FLOOR, PlacementType.CEILING);
			case CEILING -> List.of(PlacementType.WALL, PlacementType.FLOOR);
		};
	}
}
