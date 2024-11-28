package gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds.frogger;

import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.List;

public class Pugmas25FroggerUtils {

	static void buildCar(Location loc, BlockFace blockFace, Material material, int currentLength) {
		blockFace = blockFace.getOppositeFace();
		Block front = loc.getBlock();
		if (currentLength >= 0 && !front.getType().equals(Material.BLACK_STAINED_GLASS)) {
			// Front
			front.setType(material);
			front.getRelative(BlockFace.WEST).setType(Material.BLACK_CONCRETE);
			front.getRelative(BlockFace.EAST).setType(Material.BLACK_CONCRETE);
			front.getRelative(BlockFace.UP).setType(Material.WHITE_STAINED_GLASS_PANE);
			front.getRelative(BlockFace.UP).getRelative(BlockFace.WEST).setType(Material.WHITE_STAINED_GLASS_PANE);
			front.getRelative(BlockFace.UP).getRelative(BlockFace.EAST).setType(Material.WHITE_STAINED_GLASS_PANE);
		}

		// Body 1
		Block bodyOne = front.getRelative(blockFace);
		if (currentLength >= 1 && !bodyOne.getType().equals(Material.BLACK_STAINED_GLASS)) {
			buildBody(material, bodyOne);
		}

		// Body 2
		Block bodyTwo = bodyOne.getRelative(blockFace);
		if (currentLength >= 2 && !bodyTwo.getType().equals(Material.BLACK_STAINED_GLASS)) {
			buildBody(material, bodyTwo);
		}

		// End
		Block end = bodyTwo.getRelative(blockFace);
		if (currentLength >= 3 && !end.getType().equals(Material.BLACK_STAINED_GLASS)) {
			end.setType(material);
			end.getRelative(BlockFace.WEST).setType(Material.BLACK_CONCRETE);
			end.getRelative(BlockFace.EAST).setType(Material.BLACK_CONCRETE);
			end.getRelative(BlockFace.UP).setType(material);
			end.getRelative(BlockFace.UP).getRelative(BlockFace.WEST).setType(Material.AIR);
			end.getRelative(BlockFace.UP).getRelative(BlockFace.EAST).setType(Material.AIR);
		}
	}

	private static void buildBody(Material material, Block body) {
		body.setType(material);
		body.getRelative(BlockFace.WEST).setType(material);
		body.getRelative(BlockFace.EAST).setType(material);
		body.getRelative(BlockFace.UP).setType(material);
		body.getRelative(BlockFace.UP).getRelative(BlockFace.WEST).setType(material);
		body.getRelative(BlockFace.UP).getRelative(BlockFace.EAST).setType(material);
	}

	static void removeCarSlice(Block start) {
		start.setType(Material.AIR);
		start.getRelative(BlockFace.UP).setType(Material.AIR);
		start.getRelative(BlockFace.WEST).setType(Material.AIR);
		start.getRelative(BlockFace.EAST).setType(Material.AIR);
		start.getRelative(BlockFace.UP).getRelative(BlockFace.WEST).setType(Material.AIR);
		start.getRelative(BlockFace.UP).getRelative(BlockFace.EAST).setType(Material.AIR);
	}

	static void clearLogs(String regionId, Material logMaterial, Material riverMaterial) {
		List<Block> blocks = Pugmas25.get().worldedit().getBlocks(Pugmas25.get().worldguard().getRegion(regionId));
		for (Block block : blocks) {
			if (block.getType().equals(logMaterial))
				block.setType(riverMaterial);
		}
	}

	static void clearCars(String regionId) {
		List<Block> blocks = Pugmas25.get().worldedit().getBlocks(Pugmas25.get().worldguard().getRegion(regionId));
		for (Block block : blocks) {
			if (!block.getType().equals(Material.AIR))
				block.setType(Material.AIR);
		}
	}
}
