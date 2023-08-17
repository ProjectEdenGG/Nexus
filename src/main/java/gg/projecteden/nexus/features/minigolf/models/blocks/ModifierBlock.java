package gg.projecteden.nexus.features.minigolf.models.blocks;

import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolf;
import gg.projecteden.nexus.features.minigolf.MiniGolfUtils;
import gg.projecteden.nexus.features.minigolf.models.GolfBall;
import gg.projecteden.nexus.utils.StringUtils;
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

		rollDebug(golfBall);

		// Check if floating above slab
		if (MiniGolfUtils.isFloatingOnBottomSlab(location, below)) {
			golfBall.debug("ball is on top of bottom slab");
			golfBall.setGravity(true);
		}

		// Check if floating above unique collision block
		if (MiniGolfUtils.isFloatingOnUniqueCollision(location, below)) {
			golfBall.debug("ball is on top of unique collision block");
			golfBall.setGravity(true);
		}

		if (golfBall.getLocation().getY() < 0) {
			golfBall.debug("ball is in void, respawning...");
			golfBall.respawn();
			return;
		}

		checkBallSpeed(golfBall, velocity);

		// Slight friction
		velocity.multiply(0.975);
		golfBall.setVelocity(velocity);
	}

	public void handleBounce(GolfBall golfBall, Block block, BlockFace blockFace) {
		bounceDebug(golfBall);

		Vector velocity = golfBall.getVelocity();
		Snowball snowball = golfBall.getSnowball();

		boolean ballStopped = velocity.getY() >= 0.0 && velocity.length() <= 0.0;
		if (ballStopped && !golfBall.isInBounds()) {
			golfBall.debug("ball stopped out of bounds");
			golfBall.respawn();
			return;
		}

		switch (blockFace) {
			case NORTH, SOUTH -> velocity.setZ(-velocity.getZ());
			case EAST, WEST -> velocity.setX(-velocity.getX());
			case UP, DOWN -> {
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

	public boolean additionalContext(Block block) {
		return true;
	}

	public static void checkBallSpeed(GolfBall golfBall, Vector vel) {
		// Stop & respawn ball if slow enough
		if (golfBall.isMinVelocity()) {
			golfBall.getUser().debug(vel.length() != 0.0, "ball is too slow, stopping...");
			golfBall.setVelocity(new Vector(0, 0, 0));
			golfBall.setGravity(false);
			golfBall.teleportAsync(golfBall.getLocation());

			if (!golfBall.isInBounds()) {
				golfBall.debug("ball is out of bounds, respawning...");
				golfBall.respawn();
			}
		}
	}

	public String getName() {
		return StringUtils.camelCaseClass(this.getClass());
	}

	public void rollDebug(GolfBall golfBall) {
		if (golfBall.isMinVelocity())
			return;

		String debug = "on roll";
		if (!this.equals(ModifierBlockType.DEFAULT.getModifierBlock()))
			debug += " on " + this.getName();

		golfBall.debug("&o" + debug);
	}

	public void bounceDebug(GolfBall golfBall) {
		if (golfBall.isMinVelocity())
			return;

		String debug = "on bounce";
		if (!this.equals(ModifierBlockType.DEFAULT.getModifierBlock()))
			debug += " on " + this.getName();

		golfBall.debug("&o" + debug);
	}

}
