package gg.projecteden.nexus.features.minigolf.models.blocks;

import gg.projecteden.nexus.models.minigolf.GolfBall;
import gg.projecteden.nexus.utils.RandomUtils;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class BumperSkull extends ModifierSkull {

	private static final double speed = 0.5;

	@Override
	public int getSkullId() {
		return 39237;
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
		double randomX = 0;
		double randomZ = 0;

		if (!bounce) {
			super.handleRoll(golfBall, block);
			return;
		}

		switch (blockFace) {
			case NORTH, SOUTH -> {
				velocity.setZ(Math.copySign(0.5, -velocity.getZ()));
				randomX = RandomUtils.randomDouble(-speed, speed);
			}

			case EAST, WEST -> {
				velocity.setX(Math.copySign(0.5, -velocity.getX()));
				randomZ = RandomUtils.randomDouble(-speed, speed);
			}

			case UP, DOWN -> {
				velocity.setY(Math.copySign(0.5, -velocity.getY()));
				randomX = RandomUtils.randomDouble(-speed, speed);
				randomZ = RandomUtils.randomDouble(-speed, speed);
			}
		}

		velocity.add(new Vector(randomX, 0, randomZ));
		golfBall.setVelocity(velocity);
	}
}
