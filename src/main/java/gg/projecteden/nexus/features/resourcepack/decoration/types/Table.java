package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Colorable;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationType;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.List;
import java.util.Map;

public class Table extends Dyeable implements Colorable {
	@Getter
	private final TableSize size;

	public Table(String name, CustomMaterial material, TableSize size) {
		super(name, material, ColorableType.STAIN);
		this.size = size;
		this.hitboxes = size.getHitboxes();
		this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.CEILING);
		this.rotationType = RotationType.DEGREE_90;
		if (this.size.equals(TableSize._1x1))
			this.rotationType = RotationType.BOTH;
	}

	@AllArgsConstructor
	public enum TableSize {
		_1x1(Hitbox.single(Material.BARRIER)),
		_1x2(List.of(
			Hitbox.origin(),
			Hitbox.offset(BlockFace.EAST)
		)),
		_1x3(List.of(
			Hitbox.origin(),
			Hitbox.offset(BlockFace.EAST),
			Hitbox.offset(BlockFace.WEST)
		)),
		_2x2(List.of(
			Hitbox.origin(),
			Hitbox.offset(BlockFace.EAST),
			Hitbox.offset(BlockFace.NORTH),
			new Hitbox(Material.BARRIER, Map.of(BlockFace.NORTH, 1, BlockFace.EAST, 1))
		)),
		_2x3(List.of(
			Hitbox.origin(),
			Hitbox.offset(BlockFace.EAST),
			Hitbox.offset(BlockFace.NORTH),
			Hitbox.offset(BlockFace.WEST),
			new Hitbox(Material.BARRIER, Map.of(BlockFace.NORTH, 1, BlockFace.EAST, 1)),
			new Hitbox(Material.BARRIER, Map.of(BlockFace.NORTH, 1, BlockFace.WEST, 1))
		)),
		_3x3(List.of(
			Hitbox.origin(),
			Hitbox.offset(BlockFace.EAST),
			Hitbox.offset(BlockFace.NORTH),
			Hitbox.offset(BlockFace.WEST),
			Hitbox.offset(BlockFace.SOUTH),
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
