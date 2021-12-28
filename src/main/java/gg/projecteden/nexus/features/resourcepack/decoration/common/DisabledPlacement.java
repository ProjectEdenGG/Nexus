package gg.projecteden.nexus.features.resourcepack.decoration.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.block.BlockFace;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public enum DisabledPlacement {
	FLOOR(BlockFace.UP),
	WALL(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST),
	CEILING(BlockFace.DOWN),
	;

	DisabledPlacement(BlockFace... blockFaces) {
		this.blockFaces = Arrays.asList(blockFaces);
	}

	@Getter
	List<BlockFace> blockFaces;
}
