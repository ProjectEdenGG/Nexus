package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.DisabledPlacement;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DisabledRotation;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.List;
import java.util.Map;

public class Table extends Dyeable {
	@Getter
	private final TableSize size;

	public Table(String name, int modelData, TableSize size) {
		super(name, modelData);
		this.size = size;
		this.hitboxes = size.getHitboxes();
		this.disabledRotation = DisabledRotation.DEGREE_45;
		this.disabledPlacements = List.of(DisabledPlacement.WALL, DisabledPlacement.CEILING);
		this.defaultColor = getDefaultStain();
	}

	@AllArgsConstructor
	public enum TableSize {
		_1x1(Hitbox.single(Material.BARRIER)),
		_1x2(List.of(
			Hitbox.origin(Material.BARRIER),
			Hitbox.offset(Material.BARRIER, BlockFace.EAST)
		)),
		_2x2(List.of(
			Hitbox.origin(Material.BARRIER),
			Hitbox.offset(Material.BARRIER, BlockFace.EAST),
			Hitbox.offset(Material.BARRIER, BlockFace.NORTH),
			new Hitbox(Material.BARRIER, Map.of(BlockFace.NORTH, 1, BlockFace.EAST, 1))
		)),
		_2x3(List.of(
			Hitbox.origin(Material.BARRIER),
			Hitbox.offset(Material.BARRIER, BlockFace.EAST),
			Hitbox.offset(Material.BARRIER, BlockFace.NORTH),
			Hitbox.offset(Material.BARRIER, BlockFace.WEST),
			new Hitbox(Material.BARRIER, Map.of(BlockFace.NORTH, 1, BlockFace.EAST, 1)),
			new Hitbox(Material.BARRIER, Map.of(BlockFace.NORTH, 1, BlockFace.WEST, 1))
		)),
		_3x3(List.of(
			Hitbox.origin(Material.BARRIER),
			Hitbox.offset(Material.BARRIER, BlockFace.EAST),
			Hitbox.offset(Material.BARRIER, BlockFace.NORTH),
			Hitbox.offset(Material.BARRIER, BlockFace.WEST),
			Hitbox.offset(Material.BARRIER, BlockFace.SOUTH),
			new Hitbox(Material.BARRIER, Map.of(BlockFace.NORTH, 1, BlockFace.EAST, 1)),
			new Hitbox(Material.BARRIER, Map.of(BlockFace.NORTH, 1, BlockFace.WEST, 1)),
			new Hitbox(Material.BARRIER, Map.of(BlockFace.SOUTH, 1, BlockFace.EAST, 1)),
			new Hitbox(Material.BARRIER, Map.of(BlockFace.SOUTH, 1, BlockFace.WEST, 1))
		)),
		;

		@Getter
		final List<Hitbox> hitboxes;
	}
}
