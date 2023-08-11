package gg.projecteden.nexus.features.minigolf.models.blocks;

import gg.projecteden.nexus.features.minigolf.models.GolfBall;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.Set;

public class BounceBlock extends ModifierBlock {

	@Override
	public void handleRoll(GolfBall golfBall) {
		if (!golfBall.isMinVelocity())
			golfBall.getUser().debug("&oon roll on bounce block");

		golfBall.setVelocity(golfBall.getVelocity().setY(0.30));
	}

	@Override
	public void handleBounce(GolfBall golfBall, Block block, BlockFace blockFace) {
		golfBall.getUser().debug("&oon hit bounce block");
		Vector velocity = golfBall.getVelocity();

		switch (blockFace) {
			case NORTH, SOUTH -> velocity.setZ(Math.copySign(0.25, -velocity.getZ()));
			case EAST, WEST -> velocity.setX(Math.copySign(0.25, -velocity.getX()));
			case UP, DOWN -> velocity.setY(0.30);
			default -> {
				super.handleBounce(golfBall, block, blockFace);
				return;
			}
		}

		golfBall.setVelocity(velocity);
	}

	@Override
	public Set<Material> getMaterials() {
		return Set.of(Material.SLIME_BLOCK);
	}
}
