package gg.projecteden.nexus.features.minigolf.models.blocks;

import gg.projecteden.nexus.models.minigolf.GolfBall;
import gg.projecteden.nexus.utils.RandomUtils;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class TNTSkull extends ModifierSkull {

	private static final double speed = 0.5;

	@Override
	public int getSkullId() {
		return 229;
	}

	@Override
	public void handleRoll(GolfBall golfBall, Block below) {
		rollDebug(golfBall);
		explode(golfBall, false);
	}

	@Override
	public void handleBounce(GolfBall golfBall, Block block, BlockFace blockFace) {
		bounceDebug(golfBall);
		explode(golfBall, true);
	}

	private void explode(GolfBall golfBall, boolean bounce) {
		Vector velocity = golfBall.getVelocity();
		Vector randomDir = new Vector(RandomUtils.randomDouble(-speed, speed), RandomUtils.randomDouble(0.2, 0.5), RandomUtils.randomDouble(-speed, speed));

		if (bounce)
			velocity.multiply(-1).add(randomDir);
		else
			velocity.add(randomDir);

		golfBall.setVelocity(velocity);
		playBounceSound(golfBall, Sound.ENTITY_GENERIC_EXPLODE);
	}
}
