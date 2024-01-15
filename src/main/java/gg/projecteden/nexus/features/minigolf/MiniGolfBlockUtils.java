package gg.projecteden.nexus.features.minigolf;

import gg.projecteden.nexus.utils.BlockUtils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;

public class MiniGolfBlockUtils {

	public void rotate(Block block, boolean clockwise) {
		BlockData blockData = block.getBlockData();
		BlockFace newFacing = null;

		if (blockData instanceof Directional directional) {
			newFacing = BlockUtils.rotate(directional.getFacing(), directional.getFaces().stream().toList(), clockwise);
			directional.setFacing(newFacing);
		}

		if (newFacing != null)
			block.setBlockData(blockData);

	}
}
