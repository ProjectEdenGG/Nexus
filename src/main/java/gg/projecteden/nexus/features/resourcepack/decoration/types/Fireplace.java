package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.MultiBlock;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.FloorThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.List;
import java.util.Map;

@MultiBlock
public class Fireplace extends FloorThing {
	// TODO: Move to HitboxEnums
	private static final List<Hitbox> hitboxes = List.of(
		Hitbox.origin(Material.BARRIER),
		Hitbox.offset(Material.BARRIER, BlockFace.WEST),
		Hitbox.offset(Material.BARRIER, BlockFace.EAST),
		Hitbox.offset(Material.BARRIER, BlockFace.UP),
		new Hitbox(Material.BARRIER, Map.of(BlockFace.WEST, 1, BlockFace.UP, 1)),
		new Hitbox(Material.BARRIER, Map.of(BlockFace.EAST, 1, BlockFace.UP, 1)),
		new Hitbox(Hitbox.light(15), Map.of(BlockFace.SOUTH, 1, BlockFace.UP, 1))
	);

	public Fireplace(String name, CustomMaterial material) {
		super(name, material, hitboxes);
		this.rotationType = RotationType.DEGREE_90;
	}
}
