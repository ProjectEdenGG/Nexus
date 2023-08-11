package gg.projecteden.nexus.features.minigolf.models.blocks;

import gg.projecteden.nexus.features.minigolf.models.GolfBall;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.Set;

public class DefaultBlock extends ModifierBlock {

	@Override
	public void handleRoll(GolfBall golfBall) {
		super.handleRoll(golfBall);
	}

	@Override
	public void handleBounce(GolfBall golfBall, BlockFace blockFace) {
		super.handleBounce(golfBall, blockFace);
	}

	@Override
	public Set<Material> getMaterials() {
		return null;
	}
}
