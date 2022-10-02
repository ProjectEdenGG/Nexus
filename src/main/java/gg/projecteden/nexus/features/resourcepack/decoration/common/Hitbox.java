package gg.projecteden.nexus.features.resourcepack.decoration.common;

import gg.projecteden.nexus.utils.Utils.ItemFrameRotation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Light;
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
	int lightLevel = 0;

	public Hitbox(@NotNull Material material, Map<BlockFace, Integer> offset) {
		this.material = material;
		this.offsets = offset;
	}

	public Hitbox(LightHitbox lightHitbox) {
		this(lightHitbox, new HashMap<>());
	}

	public Hitbox(@NotNull LightHitbox lightHitbox, Map<BlockFace, Integer> offsets) {
		this.material = lightHitbox.getMaterial();
		this.offsets = offsets;
		this.lightLevel = lightHitbox.getLevel();
	}

	public Hitbox(@NotNull Material material, Map<BlockFace, Integer> offsets, int lightLevel) {
		this.material = material;
		this.offsets = offsets;
		this.lightLevel = lightLevel;
	}

	public static Hitbox origin() {
		return new Hitbox(Material.BARRIER);
	}

	public static Hitbox origin(Material material) {
		return new Hitbox(material);
	}

	public static Hitbox origin(LightHitbox light) {
		return new Hitbox(light);
	}

	public static Hitbox offset(BlockFace blockFace) {
		return offset(Material.BARRIER, blockFace, 1);
	}

	public static Hitbox offset(Material material, BlockFace blockFace) {
		return offset(material, blockFace, 1);
	}

	public static Hitbox offset(LightHitbox light, BlockFace blockFace) {
		return offset(light, blockFace, 1);
	}

	public static Hitbox offset(BlockFace blockFace, int offset) {
		return offset(Material.BARRIER, blockFace, offset);
	}

	public static Hitbox offset(BlockFace blockFace1, BlockFace blockFace2) {
		return offset(blockFace1, 1, blockFace2, 1);
	}

	public static Hitbox offset(BlockFace blockFace1, int offset1, BlockFace blockFace2, int offset2) {
		return new Hitbox(Material.BARRIER, Map.of(blockFace1, offset1, blockFace2, offset2));
	}

	public static Hitbox offset(Material material, BlockFace blockFace, int offset) {
		return new Hitbox(material, Map.of(blockFace, offset));
	}

	public static Hitbox offset(LightHitbox light, BlockFace blockFace, int offset) {
		return new Hitbox(light, Map.of(blockFace, offset));
	}

	public static List<Hitbox> single() {
		return single(Material.BARRIER);
	}

	public static List<Hitbox> single(Material material) {
		return Collections.singletonList(origin(material));
	}

	public static List<Hitbox> single(LightHitbox light) {
		return Collections.singletonList(origin(light));
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

	public static List<Hitbox> rotateHitboxes(DecorationConfig decorationConfig, ItemFrame itemFrame) {
		return rotateHitboxes(decorationConfig, ItemFrameRotation.of(itemFrame).getBlockFace());
	}

	public static List<Hitbox> rotateHitboxes(DecorationConfig decorationConfig, BlockFace blockFace) {
		return rotateHitboxes(decorationConfig.getHitboxes(), blockFace);
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

			result.add(new Hitbox(material, offsetsRotated, hitbox.getLightLevel()));
		}

		return result;
	}

	public static void place(List<Hitbox> hitboxes, Location origin, BlockFace blockFace) {
		hitboxes = rotateHitboxes(hitboxes, blockFace);

		for (Hitbox hitbox : hitboxes) {
			Material material = hitbox.getMaterial();
			if (isNullOrAir(material))
				material = Material.AIR;

			Block offsetBlock = hitbox.getOffsetBlock(origin);
			offsetBlock.setType(material);

			if (material == Material.LIGHT) {
				Light light = (Light) offsetBlock.getBlockData();
				light.setLevel(hitbox.getLightLevel());

				offsetBlock.setBlockData(light);
			}
		}
	}

	public static void destroy(Decoration decoration) {
		final List<Hitbox> hitboxes = rotateHitboxes(decoration.getConfig().getHitboxes(), decoration.getRotation().getBlockFace());

		for (Hitbox hitbox : hitboxes) {
			Block block = hitbox.getOffsetBlock(decoration.getOrigin());
			if (hitbox.getMaterial() == block.getType())
				block.setType(Material.AIR);
		}
	}

	public Block getOffsetBlock(Location origin) {
		Block offsetBlock = origin.clone().getBlock();
		for (BlockFace _blockFace : offsets.keySet()) {
			offsetBlock = offsetBlock.getRelative(_blockFace, offsets.get(_blockFace));
		}

		return offsetBlock;
	}

	@AllArgsConstructor
	public static class LightHitbox {
		@Getter
		private final Material material = Material.LIGHT;
		@Getter
		private final int level;
	}

	public static LightHitbox light() {
		return light(0);
	}

	public static LightHitbox light(int level) {
		return new LightHitbox(level);
	}
}
