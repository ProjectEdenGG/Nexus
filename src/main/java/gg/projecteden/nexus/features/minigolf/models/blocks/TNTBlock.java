package gg.projecteden.nexus.features.minigolf.models.blocks;

import gg.projecteden.nexus.features.minigolf.models.GolfBall;
import gg.projecteden.nexus.utils.RandomUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class TNTBlock extends ModifierBlock {

	private static final double speed = 0.75;

	@Override
	public void handleRoll(GolfBall golfBall, Block below) {
		Vector velocity = golfBall.getVelocity();
		Vector randomDir = explode(golfBall);
		velocity.add(randomDir);
		golfBall.setVelocity(velocity);

	}

	@Override
	public void handleBounce(GolfBall golfBall, Block block, BlockFace blockFace) {
		Vector velocity = golfBall.getVelocity();
		Vector randomDir = explode(golfBall);
		velocity.multiply(-1).add(randomDir);
		golfBall.setVelocity(velocity);

	}

	@Override
	public Set<Material> getMaterials() {
		return Set.of(Material.TNT);
	}

	@NotNull
	private Vector explode(GolfBall golfBall) {
		playBounceSound(golfBall, Sound.ENTITY_GENERIC_EXPLODE);
		return new Vector(RandomUtils.randomDouble(-speed, speed), 0.5, RandomUtils.randomDouble(-speed, speed));
	}
}
