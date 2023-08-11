package gg.projecteden.nexus.features.minigolf.models.blocks;

import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolf;
import gg.projecteden.nexus.features.minigolf.MiniGolfUtils;
import gg.projecteden.nexus.features.minigolf.models.GolfBall;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Snowball;
import org.bukkit.util.Vector;

import java.util.Set;

public abstract class ModifierBlock {

	public void handleRoll(GolfBall golfBall) {
		Vector velocity = golfBall.getVelocity();
		Block below = golfBall.getBlockBelow();
		Location location = golfBall.getLocation();

		if (!golfBall.isMinVelocity())
			golfBall.getUser().debug("&oon roll");

		// Check if floating above slab
		if (MiniGolfUtils.isBottomSlab(below) && location.getY() > below.getY() + 0.5) {
			golfBall.getUser().debug("ball is on top of bottom slab");
			golfBall.setGravity(true);
		}

		// Check if floating below slab
//		if(MiniGolfUtils.isTopSlab(location.getBlock()) && location.getY() >= location.getBlock().getY() + 0.5) {
//			golfBall.getUser().debug("ball is inside of top slab");
//			golfBall.teleportAsync(golfBall.getLocation().subtract(0, 0.05, 0));
//			golfBall.setGravity(true);
//		}

		if (golfBall.getLocation().getY() < 0) {
			golfBall.getUser().debug("ball is in void, respawning...");
			MiniGolfUtils.respawnBall(golfBall);
			return;
		}

		checkBallSpeed(golfBall, velocity);

		// Slight friction
		velocity.multiply(0.975);
		golfBall.setVelocity(velocity);
	}

	public void handleBounce(GolfBall golfBall, Block block, BlockFace blockFace) {
		golfBall.debug("&oon bounce");

		Vector velocity = golfBall.getVelocity();
		Snowball snowball = golfBall.getSnowball();

		// TODO: Proper bounce off floor skull rotation?
		if (MaterialTag.FLOOR_SKULLS.isTagged(block)) {
			golfBall.debug("floor skull");
		}

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
					golfBall.debug("ball is no longer bouncing");
					velocity.setY(0);
					snowball.teleportAsync(snowball.getLocation().add(0, MiniGolf.getFloorOffset(), 0));
					snowball.setGravity(false);
				}
			}
		}

		golfBall.getSnowball().setVelocity(velocity);
	}

	public abstract Set<Material> getMaterials();

	public static void checkBallSpeed(GolfBall golfBall, Vector vel) {
		// Stop & respawn ball if slow enough
		if (golfBall.isMinVelocity()) {
			golfBall.getUser().debug(vel.length() != 0.0, "ball is too slow, stopping...");
			golfBall.setVelocity(new Vector(0, 0, 0));
			golfBall.setGravity(false);
			golfBall.teleportAsync(golfBall.getLocation());

			if (!golfBall.isInBounds()) {
				golfBall.getUser().debug("ball is out of bounds, respawning...");
				MiniGolfUtils.respawnBall(golfBall);
			}
		}
	}

}
