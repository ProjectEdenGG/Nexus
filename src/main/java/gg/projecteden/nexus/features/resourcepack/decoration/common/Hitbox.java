package gg.projecteden.nexus.features.resourcepack.decoration.common;

import gg.projecteden.nexus.utils.ItemUtils;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class Hitbox {
	@NonNull Material material;
	Map<BlockFace, Integer> offsets = new HashMap<>();

	public Hitbox(@NotNull Material material, Map<BlockFace, Integer> offset) {
		this.material = material;
		this.offsets = offset;
	}

	public static Hitbox origin(Material material) {
		return new Hitbox(material);
	}

	public static List<Hitbox> single(Material material) {
		return Collections.singletonList(origin(material));
	}

	public static List<Hitbox> NONE() {
		return Collections.singletonList(new Hitbox(Material.AIR));
	}

	private static final List<BlockFace> directions = List.of(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);

	private static BlockFace rotateClockwise(BlockFace blockFace) {
		int size = directions.size() - 1;
		int index = (directions.indexOf(blockFace) + 1);

		if (index > size)
			index = 0;

		return directions.get(index);
	}

	public static List<Hitbox> rotate(List<Hitbox> hitboxes, BlockFace blockFace) {
		int ndx = directions.indexOf(blockFace);
		List<Hitbox> result = new ArrayList<>();

		for (Hitbox hitbox : hitboxes) {
			Material material = hitbox.getMaterial();
			Map<BlockFace, Integer> offsets = hitbox.getOffsets();
			if (offsets.isEmpty()) {
				result.add(hitbox);
				continue;
			}

			// Rotate
			Map<BlockFace, Integer> offsetsRotated = new HashMap<>();
			for (BlockFace face : offsets.keySet()) {
				BlockFace faceRotated = face;
				if (directions.contains(face)) {
					for (int i = 0; i < ndx; i++)
						faceRotated = rotateClockwise(faceRotated);
				}

				offsetsRotated.put(faceRotated, offsets.get(face));
			}

			result.add(new Hitbox(material, offsetsRotated));
		}

		return result;
	}

	public static void place(List<Hitbox> hitboxes, Location origin, BlockFace blockFace) {
		hitboxes = rotate(hitboxes, blockFace);

		for (Hitbox hitbox : hitboxes) {
			Material material = hitbox.getMaterial();

			Block offsetBlock = origin.clone().getBlock();
			Map<BlockFace, Integer> offsets = hitbox.getOffsets();
			for (BlockFace _blockFace : offsets.keySet()) {
				offsetBlock = offsetBlock.getRelative(_blockFace, offsets.get(_blockFace));
			}

			if (ItemUtils.isNullOrAir(material))
				material = Material.AIR;

			offsetBlock.setType(material);
		}
	}

	public static void destroy(List<Hitbox> hitboxes, Location origin, BlockFace blockFace) {
		hitboxes = rotate(hitboxes, blockFace);

		for (Hitbox hitbox : hitboxes) {
			Block offsetBlock = origin.clone().getBlock();
			Map<BlockFace, Integer> offsets = hitbox.getOffsets();
			for (BlockFace _blockFace : offsets.keySet()) {
				offsetBlock = offsetBlock.getRelative(_blockFace, offsets.get(_blockFace));
			}

			offsetBlock.setType(Material.AIR);
		}
	}
}
