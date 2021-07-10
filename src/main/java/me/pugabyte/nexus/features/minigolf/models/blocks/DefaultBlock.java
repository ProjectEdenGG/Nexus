package me.pugabyte.nexus.features.minigolf.models.blocks;

import me.pugabyte.nexus.features.minigolf.MiniGolfUtils;
import me.pugabyte.nexus.features.minigolf.models.GolfBall;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.Set;

public class DefaultBlock extends ModifierBlock {
	@Override
	public void handleRoll(GolfBall golfBall) {
		golfBall.getUser().debug("&oon roll on default block");

		Vector velocity = golfBall.getVelocity();
		Block below = golfBall.getBlockBelow();
		Location location = golfBall.getLocation();

		// Check if floating above slab
		if (MiniGolfUtils.isBottomSlab(below) && location.getY() > below.getY() + 0.5) {
			golfBall.getUser().debug("ball is ontop of bottom slab");
			golfBall.setGravity(true);
		}

		// Check if floating below slab
//		if(MiniGolfUtils.isTopSlab(location.getBlock()) && location.getY() >= location.getBlock().getY() + 0.5) {
//			golfBall.getUser().debug("ball is inside of top slab");
//			golfBall.teleport(golfBall.getLocation().subtract(0, 0.05, 0));
//			golfBall.setGravity(true);
//		}


		if (golfBall.getLocation().getY() < 0) {
			golfBall.getUser().debug("ball is in void, respawning...");
			MiniGolfUtils.respawnBall(golfBall);
			return;
		}

		// Stop & respawn ball if slow enough
		if (golfBall.isMinVelocity()) {
			golfBall.getUser().debug(velocity.length() != 0.0, "ball is too slow, stopping...");
			golfBall.setVelocity(new Vector(0, 0, 0));
			golfBall.setGravity(false);
			golfBall.teleport(golfBall.getLocation());

			if (!golfBall.isInBounds()) {
				golfBall.getUser().debug("ball is out of bounds, respawning...");
				MiniGolfUtils.respawnBall(golfBall);
			}

			return;
		}

		// Slight friction
		velocity.multiply(0.975);
		golfBall.setVelocity(velocity);
	}

	@Override
	public Set<Material> getMaterials() {
		return null;
	}
}
