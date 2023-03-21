package gg.projecteden.nexus.features.resourcepack.decoration.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.List;

public class HitboxEnums {

	public interface CustomHitbox {
		List<Hitbox> getHitboxes();
	}

	@AllArgsConstructor
	public enum Shape implements CustomHitbox {
		_1x1(Hitbox.single()),

		_1x1_LIGHT(Hitbox.single(Material.LIGHT)),

		_1x2V(List.of(
			Hitbox.origin(),
			Hitbox.offset(BlockFace.UP))
		),

		_1x2V_LIGHT(List.of(
			Hitbox.origin(Material.LIGHT),
			Hitbox.offset(Material.LIGHT, BlockFace.UP))
		),


		_1x3V(List.of(
			Hitbox.origin(),
			Hitbox.offset(BlockFace.UP, 1),
			Hitbox.offset(BlockFace.UP, 2)
		)),

		_1x3V_LIGHT(List.of(
			Hitbox.origin(Material.LIGHT),
			Hitbox.offset(Material.LIGHT, BlockFace.UP, 1),
			Hitbox.offset(Material.LIGHT, BlockFace.UP, 2)
		)),

		_1x2H(List.of(
			Hitbox.origin(),
			Hitbox.offset(BlockFace.EAST)
		)),

		_1x2H_LIGHT(List.of(
			Hitbox.origin(Material.LIGHT),
			Hitbox.offset(Material.LIGHT, BlockFace.EAST)
		)),

		_1x3H(List.of(
			Hitbox.origin(),
			Hitbox.offset(BlockFace.EAST),
			Hitbox.offset(BlockFace.WEST)
		)),

		_1x3H_LIGHT(List.of(
			Hitbox.origin(Material.LIGHT),
			Hitbox.offset(Material.LIGHT, BlockFace.EAST),
			Hitbox.offset(Material.LIGHT, BlockFace.WEST)
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
	public enum Unique implements CustomHitbox {
		DRUM_KIT(List.of(
			Hitbox.origin(),
			Hitbox.offset(BlockFace.EAST, 1),
			Hitbox.offset(BlockFace.WEST, 1),
			Hitbox.offset(BlockFace.EAST, 1, BlockFace.SOUTH, 1),
			Hitbox.offset(BlockFace.WEST, 1, BlockFace.SOUTH, 1)
		)),
		PIANO_GRAND(List.of(
			Hitbox.origin(),
			Hitbox.offset(BlockFace.WEST, 1),
			Hitbox.offset(BlockFace.NORTH, 1),
			Hitbox.offset(BlockFace.EAST, 1),
			Hitbox.offset(BlockFace.SOUTH, 1),
			Hitbox.offset(BlockFace.SOUTH, 1, BlockFace.EAST, 1),
			Hitbox.offset(BlockFace.SOUTH, 1, BlockFace.WEST, 1),
			Hitbox.offset(BlockFace.NORTH, 1, BlockFace.WEST, 1),
			Hitbox.offset(BlockFace.UP, 1),
			Hitbox.offset(BlockFace.UP, 1, BlockFace.WEST, 1),
			Hitbox.offset(BlockFace.UP, 1, BlockFace.NORTH, 1),
			Hitbox.offset(BlockFace.UP, 1, BlockFace.EAST, 1),
			Hitbox.offset(BlockFace.UP, 1, BlockFace.NORTH, 1, BlockFace.WEST, 1)
		)),
		HANGING_BANNER_1x2V(List.of(
			Hitbox.origin(Material.LIGHT),
			Hitbox.offset(Material.LIGHT, BlockFace.DOWN, 1)
		)),
		HANGING_BANNER_1x3V(List.of(
			Hitbox.origin(Material.LIGHT),
			Hitbox.offset(Material.LIGHT, BlockFace.DOWN, 1),
			Hitbox.offset(Material.LIGHT, BlockFace.DOWN, 2)
		)),
		GIANT_CANDY_CANE(List.of(
			Hitbox.origin(Material.CHAIN),
			Hitbox.offset(Material.CHAIN, BlockFace.UP, 1),
			Hitbox.offset(Material.CHAIN, BlockFace.UP, 2)
		)),
		;

		@Getter
		final List<Hitbox> hitboxes;
	}
}
