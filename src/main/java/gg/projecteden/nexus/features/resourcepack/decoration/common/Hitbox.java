package gg.projecteden.nexus.features.resourcepack.decoration.common;

import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.Nullables;
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
import org.bukkit.entity.Player;
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

	public static Hitbox origin(Material material) {
		return new Hitbox(material);
	}

	public static Hitbox origin(LightHitbox light) {
		return new Hitbox(light);
	}

	public static Hitbox offset(Material material, BlockFace face) {
		return offset(material, face, 1);
	}

	public static Hitbox offset(LightHitbox light, BlockFace face) {
		return offset(light, face, 1);
	}

	public static Hitbox offset(Material material, BlockFace face, int offset) {
		return new Hitbox(material, Map.of(face, offset));
	}

	public static Hitbox offset(LightHitbox light, BlockFace face, int offset) {
		return new Hitbox(light, Map.of(face, offset));
	}

	// BARRIER

	public static Hitbox origin() {
		return new Hitbox(Material.BARRIER);
	}

	public static Hitbox offset(BlockFace face) {
		return offset(Material.BARRIER, face, 1);
	}

	public static Hitbox offset(BlockFace face, int offset) {
		return offset(Material.BARRIER, face, offset);
	}

	public static Hitbox offset(BlockFace face1, BlockFace face2) {
		return offset(face1, 1, face2, 1);
	}

	public static Hitbox offset(BlockFace face1, int o1, BlockFace face2, int o2) {
		return new Hitbox(Material.BARRIER, Map.of(face1, o1, face2, o2));
	}

	public static Hitbox offset(BlockFace face1, int o1, BlockFace face2, int o2, BlockFace face3, int o3) {
		return new Hitbox(Material.BARRIER, Map.of(face1, o1, face2, o2, face3, o3));
	}

	public static Hitbox offset(BlockFace face1, int o1, BlockFace face2, int o2, BlockFace face3, int o3, BlockFace face4, int o4) {
		return new Hitbox(Material.BARRIER, Map.of(face1, o1, face2, o2, face3, o3, face4, o4));
	}

	// LIGHT

	public static Hitbox originLight() {
		return new Hitbox(Material.LIGHT);
	}

	public static Hitbox originLight(int lightLevel) {
		return new Hitbox(Hitbox.light(lightLevel));
	}

	public static Hitbox offsetLight(BlockFace face) {
		return offset(Material.LIGHT, face, 1);
	}

	public static Hitbox offsetLight(int lightLevel, BlockFace face) {
		return offset(Hitbox.light(lightLevel), face, 1);
	}

	public static Hitbox offsetLight(BlockFace face, int offset) {
		return offset(Material.LIGHT, face, offset);
	}

	public static Hitbox offsetLight(int lightLevel, BlockFace face, int offset) {
		return offset(Hitbox.light(lightLevel), face, offset);
	}

	public static Hitbox offsetLight(BlockFace face1, BlockFace face2) {
		return offsetLight(face1, 1, face2, 1);
	}

	public static Hitbox offsetLight(int lightLevel, BlockFace face1, BlockFace face2) {
		return offsetLight(lightLevel, face1, 1, face2, 1);
	}

	public static Hitbox offsetLight(BlockFace face1, int o1, BlockFace face2, int o2) {
		return new Hitbox(Material.LIGHT, Map.of(face1, o1, face2, o2));
	}

	public static Hitbox offsetLight(int lightLevel, BlockFace face1, int o1, BlockFace face2, int o2) {
		return new Hitbox(Hitbox.light(lightLevel), Map.of(face1, o1, face2, o2));
	}

	public static Hitbox offsetLight(BlockFace face1, int o1, BlockFace face2, int o2, BlockFace face3, int o3) {
		return new Hitbox(Material.LIGHT, Map.of(face1, o1, face2, o2, face3, o3));
	}

	public static Hitbox offsetLight(int lightLevel, BlockFace face1, int o1, BlockFace face2, int o2, BlockFace face3, int o3) {
		return new Hitbox(Hitbox.light(lightLevel), Map.of(face1, o1, face2, o2, face3, o3));
	}

	public static Hitbox offsetLight(BlockFace face1, int o1, BlockFace face2, int o2, BlockFace face3, int o3, BlockFace face4, int o4) {
		return new Hitbox(Material.LIGHT, Map.of(face1, o1, face2, o2, face3, o3, face4, o4));
	}

	public static Hitbox offsetLight(int lightLevel, BlockFace face1, int o1, BlockFace face2, int o2, BlockFace face3, int o3, BlockFace face4, int o4) {
		return new Hitbox(Hitbox.light(lightLevel), Map.of(face1, o1, face2, o2, face3, o3, face4, o4));
	}

	//

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

	//

	public static List<Hitbox> rotateHitboxes(DecorationConfig decorationConfig, ItemFrame itemFrame) {
		return rotateHitboxes(decorationConfig, ItemFrameRotation.of(itemFrame).getBlockFace());
	}

	public static List<Hitbox> rotateHitboxes(DecorationConfig decorationConfig, BlockFace face) {
		return rotateHitboxes(decorationConfig.getHitboxes(), face);
	}

	public static List<Hitbox> rotateHitboxes(List<Hitbox> hitboxes, BlockFace face) {
		return rotate(hitboxes, face);
	}

	private static List<Hitbox> rotate(List<Hitbox> hitboxes, BlockFace face) {
		int ndx = BlockUtils.cardinals.indexOf(face);
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
			for (BlockFace _face : offsets.keySet()) {
				BlockFace faceRotated = _face;
				if (BlockUtils.cardinals.contains(_face)) {
					for (int i = 0; i < ndx; i++)
						faceRotated = BlockUtils.rotateClockwise(faceRotated);
				}

				offsetsRotated.put(faceRotated, offsets.get(_face));
			}

			result.add(new Hitbox(material, offsetsRotated, hitbox.getLightLevel()));
		}

		return result;
	}

	public static void place(Player player, List<Hitbox> hitboxes, Location origin, BlockFace face) {
		hitboxes = rotateHitboxes(hitboxes, face);

		for (Hitbox hitbox : hitboxes) {
			Material material = hitbox.getMaterial();
			if (Nullables.isNullOrAir(material)) {
				continue; // don't set hitboxes that are air
			}

			Block offsetBlock = hitbox.getOffsetBlock(origin);
			offsetBlock.setType(material);

			if (material == Material.LIGHT) {
				Light light = (Light) offsetBlock.getBlockData();
				light.setLevel(hitbox.getLightLevel());
				offsetBlock.setBlockData(light);
			}
		}
	}

	public static void destroy(Player player, Decoration decoration, BlockFace face) {
		final List<Hitbox> hitboxes = rotateHitboxes(decoration.getConfig().getHitboxes(), face);

		for (Hitbox hitbox : hitboxes) {
			Block block = hitbox.getOffsetBlock(decoration.getOrigin());
			if (hitbox.getMaterial() == block.getType()) {
				block.setType(Material.AIR);
			}
		}
	}

	public Block getOffsetBlock(Location origin) {
		Block offsetBlock = origin.clone().getBlock();
		for (BlockFace _face : offsets.keySet()) {
			offsetBlock = offsetBlock.getRelative(_face, offsets.get(_face));
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
