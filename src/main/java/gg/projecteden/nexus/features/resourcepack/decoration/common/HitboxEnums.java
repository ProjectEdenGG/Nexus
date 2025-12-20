package gg.projecteden.nexus.features.resourcepack.decoration.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.List;

public class HitboxEnums {

	public interface CustomHitbox {
		List<Hitbox> getHitboxes();

		default String getName() {
			return ((Enum<?>) this).name();
		}
	}

	@Getter
	@AllArgsConstructor
	public enum HitboxSingle implements CustomHitbox {
		_1x1_BARRIER(Hitbox.single()),
		_1x1_LIGHT(Hitbox.single(Material.LIGHT)),
		_1x1_CHAIN(Hitbox.single(Material.CHAIN)),
		_1x1_POT(Hitbox.single(Material.FLOWER_POT)),
		_1x1_HEAD(Hitbox.single(Material.PLAYER_HEAD)),
		NONE(Hitbox.NONE()),
		;

		final List<Hitbox> hitboxes;
	}

	@Getter
	@AllArgsConstructor
	public enum HitboxFloor implements CustomHitbox {
		_1x2V(List.of(
				Hitbox.origin(),
				Hitbox.offset(BlockFace.UP, 1))
		),

		_1x2V_LIGHT(List.of(
				Hitbox.originLight(),
				Hitbox.offsetLight(BlockFace.UP, 1))
		),

		_1x2V_WALL(List.of(
				Hitbox.origin(Material.STONE_BRICK_WALL),
				Hitbox.offset(Material.STONE_BRICK_WALL, BlockFace.UP, 1))
		),

		_1x2V_LIGHT_DOWN(List.of(
			Hitbox.originLight(),
			Hitbox.offsetLight(BlockFace.DOWN, 1))
		),


		_1x3V(List.of(
			Hitbox.origin(),
			Hitbox.offset(BlockFace.UP, 1),
			Hitbox.offset(BlockFace.UP, 2)
		)),

		_1x3V_LIGHT(List.of(
			Hitbox.originLight(),
			Hitbox.offsetLight(BlockFace.UP, 1),
			Hitbox.offsetLight(BlockFace.UP, 2)
		)),

		_1x4V(List.of(
			Hitbox.origin(),
			Hitbox.offset(BlockFace.UP, 1),
			Hitbox.offset(BlockFace.UP, 2),
			Hitbox.offset(BlockFace.UP, 3)
		)),

		_1x2H(List.of(
			Hitbox.origin(),
			Hitbox.offset(BlockFace.EAST, 1)
		)),

		_1x2H_LIGHT(List.of(
			Hitbox.originLight(),
			Hitbox.offsetLight(BlockFace.EAST, 1)
		)),

		_1x3H(List.of(
			Hitbox.origin(),
			Hitbox.offset(BlockFace.EAST, 1),
			Hitbox.offset(BlockFace.WEST, 1)
		)),

		_1x3H_LIGHT(List.of(
			Hitbox.originLight(),
			Hitbox.offsetLight(BlockFace.EAST, 1),
			Hitbox.offsetLight(BlockFace.WEST, 1)
		)),

		_2x2(List.of(
			Hitbox.origin(),
			Hitbox.offset(BlockFace.EAST, 1),
			Hitbox.offset(BlockFace.NORTH, 1),
			Hitbox.offset(BlockFace.NORTH, 1, BlockFace.EAST, 1)
		)),

		_2x2V(List.of(
			Hitbox.origin(),
			Hitbox.offset(BlockFace.EAST, 1),
			Hitbox.offset(BlockFace.UP, 1),
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

		_2x3H(List.of(
			Hitbox.origin(),
			Hitbox.offset(BlockFace.EAST, 1),
			Hitbox.offset(BlockFace.NORTH, 1),
			Hitbox.offset(BlockFace.WEST, 1),
			Hitbox.offset(BlockFace.NORTH, 1, BlockFace.EAST, 1),
			Hitbox.offset(BlockFace.NORTH, 1, BlockFace.WEST, 1)
		)),

		_3x3(List.of(
			Hitbox.origin(),
			Hitbox.offset(BlockFace.EAST, 1),
			Hitbox.offset(BlockFace.NORTH, 1),
			Hitbox.offset(BlockFace.WEST, 1),
			Hitbox.offset(BlockFace.SOUTH, 1),
			Hitbox.offset(BlockFace.NORTH, 1, BlockFace.EAST, 1),
			Hitbox.offset(BlockFace.NORTH, 1, BlockFace.WEST, 1),
			Hitbox.offset(BlockFace.SOUTH, 1, BlockFace.EAST, 1),
			Hitbox.offset(BlockFace.SOUTH, 1, BlockFace.WEST, 1)
		)),

		_2x2SE_CORNER(List.of(
			Hitbox.origin(),
			Hitbox.offset(BlockFace.SOUTH, 1),
			Hitbox.offset(BlockFace.EAST, 1)
		)),

		_2x3SE_CORNER(List.of(
			Hitbox.origin(),
			Hitbox.offset(BlockFace.SOUTH, 1),
			Hitbox.offset(BlockFace.EAST, 1),
			Hitbox.offset(BlockFace.EAST, 2)
		)),

		_2x3SW_CORNER(List.of(
			Hitbox.origin(),
			Hitbox.offset(BlockFace.SOUTH, 1),
			Hitbox.offset(BlockFace.WEST, 1),
			Hitbox.offset(BlockFace.WEST, 2)
		)),

		_3x3SE_CORNER(List.of(
			Hitbox.origin(),
			Hitbox.offset(BlockFace.SOUTH, 1),
			Hitbox.offset(BlockFace.SOUTH, 2),
			Hitbox.offset(BlockFace.EAST, 1),
			Hitbox.offset(BlockFace.EAST, 2)
		)),
		;

		final List<Hitbox> hitboxes;
	}

	@Getter
	@AllArgsConstructor
	public enum HitboxWall implements CustomHitbox {
		_1x1_LIGHT(Hitbox.single(Material.LIGHT)),

		_1x2H_LIGHT(List.of(
				Hitbox.originLight(),
				Hitbox.offsetLight(BlockFace.EAST, 1)
		)),

		_1x2V_LIGHT(List.of(
				Hitbox.originLight(),
				Hitbox.offsetLight(BlockFace.UP, 1)
		)),

		_2x2_LIGHT(List.of(
			Hitbox.originLight(),
			Hitbox.offsetLight(BlockFace.EAST, 1),
			Hitbox.offsetLight(BlockFace.UP, 1),
			Hitbox.offsetLight(BlockFace.EAST, 1, BlockFace.UP, 1)
		)),

		_1x3H_LIGHT(List.of(
			Hitbox.originLight(),
			Hitbox.offsetLight(BlockFace.EAST, 1),
			Hitbox.offsetLight(BlockFace.EAST, 2)
		)),

		_1x3V_LIGHT(List.of(
			Hitbox.originLight(),
			Hitbox.offsetLight(BlockFace.UP, 1),
			Hitbox.offsetLight(BlockFace.UP, 2)
		)),

		_2x3H_LIGHT(List.of(
			Hitbox.originLight(),
			Hitbox.offsetLight(BlockFace.EAST, 1),
			Hitbox.offsetLight(BlockFace.EAST, 2),
			Hitbox.offsetLight(BlockFace.UP, 1),
			Hitbox.offsetLight(BlockFace.UP, 1, BlockFace.EAST, 1),
			Hitbox.offsetLight(BlockFace.UP, 1, BlockFace.EAST, 2)
		)),

		_2x3V_LIGHT(List.of(
			Hitbox.originLight(),
			Hitbox.offsetLight(BlockFace.UP, 1),
			Hitbox.offsetLight(BlockFace.UP, 2),
			Hitbox.offsetLight(BlockFace.EAST, 1),
			Hitbox.offsetLight(BlockFace.EAST, 1, BlockFace.UP, 1),
			Hitbox.offsetLight(BlockFace.EAST, 1, BlockFace.UP, 2)
		)),

		_3x3_LIGHT(List.of(
			Hitbox.originLight(),
			Hitbox.offsetLight(BlockFace.UP, 1),
			Hitbox.offsetLight(BlockFace.UP, 2),
			Hitbox.offsetLight(BlockFace.EAST, 1),
			Hitbox.offsetLight(BlockFace.EAST, 2),
			Hitbox.offsetLight(BlockFace.EAST, 1, BlockFace.UP, 1),
			Hitbox.offsetLight(BlockFace.EAST, 1, BlockFace.UP, 2),
			Hitbox.offsetLight(BlockFace.EAST, 2, BlockFace.UP, 1),
			Hitbox.offsetLight(BlockFace.EAST, 2, BlockFace.UP, 2)

		)),

		_2x4H_LIGHT(List.of(
			Hitbox.originLight(),
			Hitbox.offsetLight(BlockFace.UP, 1),
			Hitbox.offsetLight(BlockFace.EAST, 1),
			Hitbox.offsetLight(BlockFace.EAST, 2),
			Hitbox.offsetLight(BlockFace.EAST, 3),
			Hitbox.offsetLight(BlockFace.EAST, 1, BlockFace.UP, 1),
			Hitbox.offsetLight(BlockFace.EAST, 2, BlockFace.UP, 1),
			Hitbox.offsetLight(BlockFace.EAST, 3, BlockFace.UP, 1)
		)),

		_4x4_LIGHT(List.of(
			Hitbox.originLight(),
			Hitbox.offsetLight(BlockFace.UP, 1),
			Hitbox.offsetLight(BlockFace.UP, 2),
			Hitbox.offsetLight(BlockFace.UP, 3),
			Hitbox.offsetLight(BlockFace.EAST, 1),
			Hitbox.offsetLight(BlockFace.EAST, 2),
			Hitbox.offsetLight(BlockFace.EAST, 3),
			Hitbox.offsetLight(BlockFace.EAST, 1, BlockFace.UP, 1),
			Hitbox.offsetLight(BlockFace.EAST, 1, BlockFace.UP, 2),
			Hitbox.offsetLight(BlockFace.EAST, 1, BlockFace.UP, 3),
			Hitbox.offsetLight(BlockFace.EAST, 2, BlockFace.UP, 1),
			Hitbox.offsetLight(BlockFace.EAST, 2, BlockFace.UP, 2),
			Hitbox.offsetLight(BlockFace.EAST, 2, BlockFace.UP, 3),
			Hitbox.offsetLight(BlockFace.EAST, 3, BlockFace.UP, 1),
			Hitbox.offsetLight(BlockFace.EAST, 3, BlockFace.UP, 2),
			Hitbox.offsetLight(BlockFace.EAST, 3, BlockFace.UP, 3)
		)),

		;

		final List<Hitbox> hitboxes;
	}

	@Getter
	@AllArgsConstructor
	public enum HitboxUnique implements CustomHitbox {
		HOLLY_CANDLE_BIG(List.of(Hitbox.originLight(12))),
		HOLLY_CANDLES(List.of(Hitbox.originLight(15))),

		STRING_LIGHTS(List.of(
			Hitbox.originLight(8),
			Hitbox.offsetLight(8, BlockFace.EAST, 1)
		)),

		STRING_LIGHTS_FLIPPED(List.of(
			Hitbox.originLight(8),
			Hitbox.offsetLight(8, BlockFace.WEST, 1)
		)),

		LAMP(List.of(
			Hitbox.origin(),
			Hitbox.offset(BlockFace.UP, 1),
			Hitbox.offsetLight(15, BlockFace.UP, 2)
		)),

		LAMP_SHORT(List.of(
			Hitbox.origin(),
			Hitbox.offsetLight(15, BlockFace.UP, 1)
		)),


		GIANT_CANDLE_UNLIT(List.of(
			Hitbox.origin(),
			Hitbox.offsetLight(0, BlockFace.UP, 1)
		)),

		GIANT_CANDLE_LIT(List.of(
			Hitbox.origin(),
			Hitbox.offsetLight(15, BlockFace.UP, 1)
		)),

		CARDBOARD_BOX(List.of(
				Hitbox.origin(),
				Hitbox.offset(BlockFace.EAST, 1),
				Hitbox.offset(BlockFace.NORTH, 1),
				Hitbox.offset(BlockFace.NORTH, 1, BlockFace.EAST, 1),
				Hitbox.offset(BlockFace.UP, 1),
				Hitbox.offset(BlockFace.UP, 1, BlockFace.EAST, 1),
				Hitbox.offset(BlockFace.UP, 1, BlockFace.NORTH, 1),
				Hitbox.offset(BlockFace.UP, 1, BlockFace.NORTH, 1, BlockFace.EAST, 1)
		)),

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

		FIREPLACE(List.of(
			Hitbox.origin(Material.BARRIER),
			Hitbox.offset(Material.BARRIER, BlockFace.WEST),
			Hitbox.offset(Material.BARRIER, BlockFace.EAST),
			Hitbox.offset(Material.BARRIER, BlockFace.UP),
			Hitbox.offset(BlockFace.WEST, 1, BlockFace.UP, 1),
			Hitbox.offset(BlockFace.EAST, 1, BlockFace.UP, 1),
			Hitbox.offsetLight(15, BlockFace.SOUTH, 1, BlockFace.UP, 1)
		)),

		HANGING_BANNER_1x3V(List.of(
			Hitbox.originLight(),
			Hitbox.offsetLight(BlockFace.DOWN, 1),
			Hitbox.offsetLight(BlockFace.DOWN, 2)
		)),

		GIANT_CANDY_CANE(List.of(
			Hitbox.origin(Material.CHAIN),
			Hitbox.offset(Material.CHAIN, BlockFace.UP, 1),
			Hitbox.offset(Material.CHAIN, BlockFace.UP, 2)
		)),

		GRAVESTONE_TALL(List.of(
				Hitbox.origin(Material.IRON_BARS),
				Hitbox.offset(Material.IRON_BARS, BlockFace.UP, 1)
		)),

		BEACH_CHAIR(List.of(
				Hitbox.originLight(),
				Hitbox.offsetLight(BlockFace.SOUTH, 1)
		)),

		PAPER_LANTERN_2V(List.of(
				Hitbox.origin(),
				Hitbox.offset(BlockFace.DOWN, 1),
				Hitbox.offsetLight(15, BlockFace.DOWN, 2)
		)),

		PAPER_LANTERN_3V(List.of(
				Hitbox.origin(),
				Hitbox.offset(BlockFace.DOWN, 1),
				Hitbox.offset(BlockFace.DOWN, 2),
				Hitbox.offsetLight(15, BlockFace.DOWN, 3)
		)),

		WOODEN_PICNIC_TABLE(List.of(
			Hitbox.origin().sittable(false),
			Hitbox.offset(BlockFace.EAST, 1).sittable(false),
			Hitbox.offset(BlockFace.NORTH, 1),
			Hitbox.offset(BlockFace.WEST, 1).sittable(false),
			Hitbox.offset(BlockFace.SOUTH, 1),
			Hitbox.offset(BlockFace.NORTH, 1, BlockFace.EAST, 1),
			Hitbox.offset(BlockFace.NORTH, 1, BlockFace.WEST, 1),
			Hitbox.offset(BlockFace.SOUTH, 1, BlockFace.EAST, 1),
			Hitbox.offset(BlockFace.SOUTH, 1, BlockFace.WEST, 1)
		)),

		TV(List.of(
			Hitbox.originLight(),
			Hitbox.offsetLight(BlockFace.UP, 1),
			Hitbox.offsetLight(BlockFace.WEST, 1),
			Hitbox.offsetLight(BlockFace.WEST, 1, BlockFace.UP, 1),
			Hitbox.offsetLight(BlockFace.EAST, 1),
			Hitbox.offsetLight(BlockFace.EAST, 1, BlockFace.UP, 1)
		)),

		SANTAS_TABLE(List.of(
			Hitbox.origin(),
			Hitbox.offset(BlockFace.WEST, 1),
			Hitbox.offset(BlockFace.EAST, 1),
			Hitbox.offsetLight(12, BlockFace.WEST, 1, BlockFace.UP, 1),
			Hitbox.offsetLight(12, BlockFace.EAST, 1, BlockFace.UP, 1)
		)),
		;

		final List<Hitbox> hitboxes;
	}
}
