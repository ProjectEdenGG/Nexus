package gg.projecteden.nexus.features.resourcepack.decoration.common;

import gg.projecteden.nexus.utils.Utils.ItemFrameRotation;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

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

	public static Hitbox offset(Material material, BlockFace blockFace) {
		return offset(material, blockFace, 1);
	}

	public static Hitbox offset(Material material, BlockFace blockFace, int offset) {
		return new Hitbox(material, Map.of(blockFace, offset));
	}

	public static List<Hitbox> single(Material material) {
		return Collections.singletonList(origin(material));
	}

	public static List<Hitbox> NONE() {
		return Collections.singletonList(new Hitbox(Material.AIR));
	}

	@Getter
	private static final List<BlockFace> directions = List.of(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);

	private static BlockFace rotateClockwise(BlockFace blockFace) {
		int size = directions.size() - 1;
		int index = (directions.indexOf(blockFace) + 1);

		if (index > size)
			index = 0;

		return directions.get(index);
	}

	public static List<Hitbox> rotateHitboxes(Decoration decoration, ItemFrame itemFrame) {
		return rotateHitboxes(decoration, ItemFrameRotation.of(itemFrame).getBlockFace());
	}

	public static List<Hitbox> rotateHitboxes(Decoration decoration, BlockFace blockFace) {
		return rotateHitboxes(decoration.getHitboxes(), blockFace);
	}

	public static List<Hitbox> rotateHitboxes(List<Hitbox> hitboxes, BlockFace blockFace) {
		return rotate(hitboxes, blockFace);
	}

	private static List<Hitbox> rotate(List<Hitbox> hitboxes, BlockFace blockFace) {
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
		hitboxes = rotateHitboxes(hitboxes, blockFace);

		for (Hitbox hitbox : hitboxes) {
			Material material = hitbox.getMaterial();
			if (isNullOrAir(material))
				material = Material.AIR;

			hitbox.getOffsetBlock(origin).setType(material);
		}
	}

	public static void destroy(List<Hitbox> hitboxes, Location origin, BlockFace blockFace) {
		hitboxes = rotateHitboxes(hitboxes, blockFace);

		for (Hitbox hitbox : hitboxes)
			hitbox.getOffsetBlock(origin).setType(Material.AIR);
	}

	public Block getOffsetBlock(Location origin) {
		Block offsetBlock = origin.clone().getBlock();
		for (BlockFace _blockFace : offsets.keySet()) {
			offsetBlock = offsetBlock.getRelative(_blockFace, offsets.get(_blockFace));
		}

		return offsetBlock;
	}
}
