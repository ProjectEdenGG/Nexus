package gg.projecteden.nexus.features.minigolf.models.blocks;

import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolf;
import gg.projecteden.nexus.features.minigolf.MiniGolfUtils;
import gg.projecteden.nexus.features.minigolf.models.GolfBall;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Snowball;
import org.bukkit.util.Vector;

import java.util.Set;

public abstract class ModifierBlock {

	public void handleRoll(GolfBall golfBall, Block below) {
		Vector velocity = golfBall.getVelocity();
		Location location = golfBall.getLocation();

		rollDebug(golfBall);

		// Check if floating above unique collision block
		if (MiniGolfUtils.isFloatingOnUniqueCollision(location, below)) {
			golfBall.debug("ball is on top of unique collision block");
			golfBall.setGravity(true);
		}

		// Check if floating
//		if (ModifierBlockType.GRAVITY.getMaterials().contains(golfBall.getBlock().getType()) && location.getY() > MiniGolf.getFloorOffset()) {
//			golfBall.debug("ball is floating");
//			golfBall.setGravity(true);
//		}

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
		playBounceSound(golfBall, block);
	}

	protected String getBounceSound(Block block) {
		if (Nullables.isNullOrAir(block))
			return Sound.BLOCK_STONE_HIT.getKey().getKey();

		return block.getBlockSoundGroup().getHitSound().getKey().getKey();
	}

	protected void playBounceSound(GolfBall golfBall, Block block) {
		playBounceSound(golfBall, getBounceSound(block));
	}

	protected void playBounceSound(GolfBall golfBall, Sound sound) {
		playBounceSound(golfBall, sound.getKey().getKey());
	}


	protected void playBounceSound(GolfBall golfBall, String sound) {
		new SoundBuilder(sound).location(golfBall.getLocation()).volume(0.5).play();
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
			golfBall.teleport(golfBall.getLocation());

			if (!golfBall.isInBounds()) {
				golfBall.debug("ball is out of bounds, respawning...");
				golfBall.respawn();
			}

			golfBall.setActive(false);
		}
	}

	public String getName() {
		return StringUtils.camelCaseClass(this.getClass());
	}

	public void rollDebug(GolfBall golfBall) {
		if (golfBall.isMinVelocity())
			return;

		MiniGolfUtils.debugDot(golfBall.getLocation(), getDebugDotColor());

		String debug = "on roll";
		if (!this.equals(ModifierBlockType.DEFAULT.getModifierBlock()))
			debug += " on " + this.getName();

		golfBall.debug("&o" + debug);
	}

	public ColorType getDebugDotColor() {
		return ColorType.WHITE;
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
