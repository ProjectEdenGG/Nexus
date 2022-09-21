package gg.projecteden.nexus.features.resourcepack.decoration.types.seats;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Colorable;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.MultiBlock;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Seat;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.List;
import java.util.Map;

@MultiBlock
public class Bench extends Chair implements Seat, Colorable {
	private static final List<Hitbox> hitboxes = List.of(
		Hitbox.origin(Material.BARRIER),
		new Hitbox(Material.BARRIER, Map.of(BlockFace.EAST, 1)));

	public Bench(String name, CustomMaterial material, ColorableType colorableType) {
		super(name, material, colorableType, hitboxes, null);
	}

	public Bench(String name, CustomMaterial material, ColorableType colorableType, double sitHeight) {
		super(name, material, colorableType, hitboxes, sitHeight);
	}
}
