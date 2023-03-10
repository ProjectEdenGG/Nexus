package gg.projecteden.nexus.features.resourcepack.decoration.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.block.BlockFace;

import java.util.List;

@AllArgsConstructor
public enum HitboxShape {
	_1x1(Hitbox.single()),

	_1x2V(List.of(
		Hitbox.origin(),
		Hitbox.offset(BlockFace.UP))
	),

	_1x3V(List.of(
		Hitbox.origin(),
		Hitbox.offset(BlockFace.UP, 1),
		Hitbox.offset(BlockFace.UP, 2)
	)),

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
