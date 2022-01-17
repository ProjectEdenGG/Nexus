package gg.projecteden.nexus.features.minigolf.models.blocks;

import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolf;
import gg.projecteden.nexus.features.minigolf.models.GolfBall;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Snowball;
import org.bukkit.util.Vector;

import java.util.Set;

public abstract class ModifierBlock {

	public void handleRoll(GolfBall golfBall) {
		golfBall.debug("&oon roll on generic block");
	}

	public void handleBounce(GolfBall golfBall, BlockFace blockFace) {
		golfBall.debug("&oon hit generic block: " + blockFace);
		Vector velocity = golfBall.getVelocity();
		Snowball snowball = golfBall.getSnowball();

		switch (blockFace) {
			case NORTH, SOUTH -> velocity.setZ(-velocity.getZ());
			case EAST, WEST -> velocity.setX(-velocity.getX());
			case UP, DOWN -> {
				if (velocity.getY() >= 0 && velocity.length() <= 0.01 && !golfBall.isInBounds()) {
					golfBall.debug("ball stopped out of bounds");
					golfBall.respawn();
					return;
				}

				velocity.setY(-velocity.getY());
				velocity.multiply(0.7);

				if (velocity.getY() < 0.1) {
					golfBall.debug("velocity < 0.1");
					velocity.setY(0);
					snowball.teleportAsync(snowball.getLocation().add(0, MiniGolf.getFloorOffset(), 0));
					snowball.setGravity(false);
				}
			}
		}

		golfBall.getSnowball().setVelocity(velocity);
	}

	public abstract Set<Material> getMaterials();

}
