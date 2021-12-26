package gg.projecteden.nexus.features.resourcepack.decoration.common;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.List;
import java.util.Map;

public class LargeFireplace extends Decoration {

	public LargeFireplace(String name, int modelData) {
		this.name = name;
		this.modelData = modelData;

		this.hitboxes = List.of(
			Hitbox.origin(Material.LIGHT),
			new Hitbox(Material.BARRIER, Map.of(BlockFace.WEST, 1)),
			new Hitbox(Material.BARRIER, Map.of(BlockFace.EAST, 1)),
			new Hitbox(Material.BARRIER, Map.of(BlockFace.UP, 1)),
			new Hitbox(Material.BARRIER, Map.of(BlockFace.WEST, 1, BlockFace.UP, 1)),
			new Hitbox(Material.BARRIER, Map.of(BlockFace.EAST, 1, BlockFace.UP, 1)));

		this.disabledRotation = DisabledRotation.DEGREE_45;

	}
}
