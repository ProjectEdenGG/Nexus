package gg.projecteden.nexus.features.minigolf.models.blocks;

import gg.projecteden.nexus.models.minigolf.GolfBall;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class BounceSkull extends ModifierSkull {

	@Override
	public int getSkullId() {
		return 58184;
	}

	@Override
	public void handleRoll(GolfBall golfBall, Block block) {
		rollDebug(golfBall);
		ModifierBlockType.BOUNCE_BLOCK.getModifierBlock().handleRoll(golfBall, block);
	}

	@Override
	public void handleBounce(GolfBall golfBall, Block block, BlockFace blockFace) {
		bounceDebug(golfBall);
		ModifierBlockType.BOUNCE_BLOCK.getModifierBlock().handleBounce(golfBall, block, blockFace);
	}
}
