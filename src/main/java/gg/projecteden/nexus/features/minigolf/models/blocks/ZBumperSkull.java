package gg.projecteden.nexus.features.minigolf.models.blocks;

import gg.projecteden.nexus.models.minigolf.GolfBall;
import gg.projecteden.nexus.utils.RandomUtils;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class ZBumperSkull extends ModifierSkull {

	private static final double speed = 0.5;

	@Override
	public int getSkullId() {
		return 708;
	}

	@Override
	public void handleRoll(GolfBall golfBall, Block below) {
		rollDebug(golfBall);
		bump(golfBall, below, BlockFace.UP, false);
	}

	@Override
	public void handleBounce(GolfBall golfBall, Block block, BlockFace blockFace) {
		bounceDebug(golfBall);
		bump(golfBall, block, blockFace, true);
	}

	private void bump(GolfBall golfBall, Block block, BlockFace blockFace, boolean bounce) {
		playBounceSound(golfBall, Sound.BLOCK_COPPER_HIT);

		Vector velocity = golfBall.getVelocity();

		if (!bounce) {
			super.handleRoll(golfBall, block);
			return;
		}

		switch (blockFace) {
			case NORTH, SOUTH, EAST, WEST -> velocity.setZ(Math.copySign(0.5, -velocity.getZ()));
			case UP, DOWN -> velocity.setY(Math.copySign(0.35, -velocity.getY()));
		}

		velocity.add(new Vector(0, 0, RandomUtils.randomDouble(-speed, speed)));
		golfBall.setVelocity(velocity);
	}
}
