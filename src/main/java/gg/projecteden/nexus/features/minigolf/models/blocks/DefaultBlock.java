package gg.projecteden.nexus.features.minigolf.models.blocks;

import gg.projecteden.nexus.features.minigolf.models.GolfBall;
import gg.projecteden.nexus.utils.ColorType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.Set;

public class DefaultBlock extends ModifierBlock {

	@Override
	public ColorType getDebugDotColor() {
		return ColorType.YELLOW;
	}

	@Override
	public void handleRoll(GolfBall golfBall) {
		super.handleRoll(golfBall);
	}

	@Override
	public void handleBounce(GolfBall golfBall, Block block, BlockFace blockFace) {
		super.handleBounce(golfBall, block, blockFace);
	}

	@Override
	public boolean additionalContext(Block block) {
		return super.additionalContext(block);
	}

	@Override
	public Set<Material> getMaterials() {
		return null;
	}
}
