package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.MultiBlock;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.List;
import java.util.Map;

@MultiBlock
public class Fireplace extends Decoration {
	private static final List<Hitbox> hitboxes = List.of(
		Hitbox.origin(Material.BARRIER),
		Hitbox.offset(Material.BARRIER, BlockFace.WEST),
		Hitbox.offset(Material.BARRIER, BlockFace.EAST),
		Hitbox.offset(Material.BARRIER, BlockFace.UP),
		new Hitbox(Material.BARRIER, Map.of(BlockFace.WEST, 1, BlockFace.UP, 1)),
		new Hitbox(Material.BARRIER, Map.of(BlockFace.EAST, 1, BlockFace.UP, 1)),
		Hitbox.offset(Material.LIGHT, BlockFace.SOUTH));

	public Fireplace(String name, int modelData) {
		super(name, modelData, hitboxes);
	}
}
