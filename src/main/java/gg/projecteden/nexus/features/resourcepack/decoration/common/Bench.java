package gg.projecteden.nexus.features.resourcepack.decoration.common;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.List;
import java.util.Map;

public class Bench extends MultiBlockSeat {

	public Bench(String name, int modelData) {
		super(name, modelData, List.of(Hitbox.origin(Material.BARRIER), new Hitbox(Material.BARRIER, Map.of(BlockFace.EAST, 1))));
	}
}
