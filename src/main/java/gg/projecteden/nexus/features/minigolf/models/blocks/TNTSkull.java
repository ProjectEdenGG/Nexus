package gg.projecteden.nexus.features.minigolf.models.blocks;

import gg.projecteden.nexus.features.minigolf.models.GolfBall;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class TNTSkull extends ModifierSkull {

	@Override
	public int getSkullId() {
		return 229;
	}

	@Override
	public void handleRoll(GolfBall golfBall) {
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
		Vector randomDir = new Vector(RandomUtils.randomDouble(-0.5, 0.5), RandomUtils.randomDouble(0.2, 0.6), RandomUtils.randomDouble(-0.5, 0.5));

		if (bounce)
			velocity.multiply(-1).add(randomDir);
		else
			velocity.add(randomDir);

		golfBall.setVelocity(velocity);
		new SoundBuilder(Sound.ENTITY_GENERIC_EXPLODE).location(golfBall.getLocation()).volume(0.5).play();
	}
}
