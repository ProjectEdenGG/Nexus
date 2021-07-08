package me.pugabyte.nexus.features.minigolf.models.blocks;

import me.pugabyte.nexus.features.minigolf.models.GolfBall;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.Set;

public class CatchBlock extends ModifierBlock {
	@Override
	public void handleRoll(GolfBall golfBall) {
		golfBall.getUser().debug("&oon roll on catch block");

		Vector velocity = golfBall.getVelocity();
		velocity.setY(0);
		golfBall.setVelocity(velocity);
	}

	@Override
	public void handleBounce(GolfBall golfBall, BlockFace blockFace) {
		golfBall.getUser().debug("&oon hit catch block");
		Vector velocity = golfBall.getVelocity();

		switch (blockFace) {
			case NORTH, SOUTH -> velocity.setZ(0);
			case EAST, WEST -> velocity.setX(0);
			case UP, DOWN -> velocity.setY(0);
			default -> {
				super.handleBounce(golfBall, blockFace);
				return;
			}
		}

		golfBall.setVelocity(velocity);
	}

	@Override
	public Set<Material> getMaterials() {
		return Set.of(Material.SOUL_SOIL);
	}
}
