package gg.projecteden.nexus.features.minigolf.models.blocks;

import gg.projecteden.nexus.features.minigolf.models.GolfBall;
import gg.projecteden.nexus.utils.RandomUtils;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class BumperSkull extends ModifierSkull {

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

//		if(blockFace == BlockFace.DOWN){
//			super.handleBounce(golfBall, block, blockFace);
//			return;
//		}

		switch (blockFace) {
			case NORTH, SOUTH -> {
				velocity.setZ(Math.copySign(0.5, -velocity.getZ()));
				randomX = RandomUtils.randomDouble(-1, 1);
			}

			case EAST, WEST -> {
				velocity.setX(Math.copySign(0.5, -velocity.getX()));
				randomZ = RandomUtils.randomDouble(-1, 1);
			}

			case UP, DOWN -> {
				velocity.setY(Math.copySign(0.5, -velocity.getY()));
				randomX = RandomUtils.randomDouble(-1, 1);
				randomZ = RandomUtils.randomDouble(-1, 1);
			}
		}

		velocity.add(new Vector(randomX, 0, randomZ));
		golfBall.setVelocity(velocity);
	}
}
