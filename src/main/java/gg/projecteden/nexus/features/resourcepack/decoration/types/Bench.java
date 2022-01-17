package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.MultiBlock;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Seat;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.List;
import java.util.Map;

@MultiBlock
public class Bench extends Chair implements Seat {
	private static final List<Hitbox> hitboxes = List.of(
		Hitbox.origin(Material.BARRIER),
		new Hitbox(Material.BARRIER, Map.of(BlockFace.EAST, 1)));

	public Bench(String name, int modelData, DyedPart dyedPart) {
		super(name, modelData, dyedPart, hitboxes);
		this.defaultColor = getDefaultStain();
	}
}
