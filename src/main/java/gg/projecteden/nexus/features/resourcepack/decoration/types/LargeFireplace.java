package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.MultiBlock;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.List;
import java.util.Map;

@MultiBlock
public class LargeFireplace extends Decoration {
	private static final List<Hitbox> hitboxes = List.of(
		Hitbox.origin(Material.BARRIER),
		new Hitbox(Material.BARRIER, Map.of(BlockFace.WEST, 1)),
		new Hitbox(Material.BARRIER, Map.of(BlockFace.EAST, 1)),
		new Hitbox(Material.BARRIER, Map.of(BlockFace.UP, 1)),
		new Hitbox(Material.BARRIER, Map.of(BlockFace.WEST, 1, BlockFace.UP, 1)),
		new Hitbox(Material.BARRIER, Map.of(BlockFace.EAST, 1, BlockFace.UP, 1)),
		new Hitbox(Material.LIGHT, Map.of(BlockFace.SOUTH, 1)));

	public LargeFireplace(String name, int modelData) {
		super(name, modelData, hitboxes);
	}
}
