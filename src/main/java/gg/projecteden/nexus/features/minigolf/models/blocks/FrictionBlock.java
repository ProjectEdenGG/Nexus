package gg.projecteden.nexus.features.minigolf.models.blocks;

import gg.projecteden.nexus.models.minigolf.GolfBall;
import gg.projecteden.nexus.utils.ColorType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.Set;

public class FrictionBlock extends ModifierBlock {

	@Override
	public ColorType getDebugDotColor() {
		return ColorType.BROWN;
	}

	@Override
	public void handleRoll(GolfBall golfBall, Block below) {
		rollDebug(golfBall);

		Vector velocity = golfBall.getVelocity();
		velocity.multiply(0.8);
		golfBall.setVelocity(velocity);

		checkBallSpeed(golfBall, velocity);
	}

	@Override
	public void handleBounce(GolfBall golfBall, Block block, BlockFace blockFace) {
		golfBall.debug("&oon hit friction block");
		Vector velocity = golfBall.getVelocity();

		switch (blockFace) {
			case NORTH, SOUTH -> velocity.setZ(0);
			case EAST, WEST -> velocity.setX(0);
			case UP, DOWN -> velocity.setY(0);
			default -> {
				super.handleBounce(golfBall, block, blockFace);
				return;
			}
		}

		golfBall.setVelocity(velocity);
	}

	@Override
	public Set<Material> getMaterials() {
		return Set.of(Material.SAND, Material.RED_SAND);
	}
}
