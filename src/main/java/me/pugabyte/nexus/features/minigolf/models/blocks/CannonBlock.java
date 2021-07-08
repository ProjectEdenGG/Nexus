package me.pugabyte.nexus.features.minigolf.models.blocks;

import com.destroystokyo.paper.ParticleBuilder;
import me.pugabyte.nexus.features.minigolf.MiniGolfUtils;
import me.pugabyte.nexus.features.minigolf.models.GolfBall;
import me.pugabyte.nexus.utils.LocationUtils;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.SoundBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.util.Vector;

import java.util.Set;

public class CannonBlock extends ModifierBlock {

	@Override
	public void handleRoll(GolfBall golfBall) {
		golfBall.getUser().debug("&oon roll on cannon block");

		Vector velocity = golfBall.getVelocity();
		Block below = golfBall.getBlockBelow();

		if (!(below.getBlockData() instanceof Directional directional))
			return;

		BlockFace facing = directional.getFacing();
		below = below.getRelative(facing.getOppositeFace()); // TODO: look for a sign anywhere around the block
		if (!MaterialTag.SIGNS.isTagged(below.getType()))
			return;

		Sign sign = (Sign) below.getState();
		String heightStr = sign.getLine(2).replaceAll("height", "");
		String powerStr = sign.getLine(3).replaceAll("power", "");
		try {
			Location newLoc = LocationUtils.getCenteredLocation(below.getRelative(facing).getLocation());
			golfBall.setVelocity(new Vector(0, 0, 0));
			golfBall.teleport(LocationUtils.getCenteredLocation(newLoc));
			golfBall.setGravity(true);

			double height = Double.parseDouble(heightStr);
			double power = Double.parseDouble(powerStr);
			Vector newVel = MiniGolfUtils.getDirection(facing.getOppositeFace(), power);

			golfBall.setVelocity(velocity.multiply(9.3).add(newVel).setY(height));
			new SoundBuilder(Sound.ENTITY_GENERIC_EXPLODE).location(golfBall.getLocation()).volume(3.0).play();
			new ParticleBuilder(Particle.EXPLOSION_NORMAL).location(golfBall.getLocation()).count(25).spawn();
		} catch (Exception ignored) {

		}
	}

	@Override
	public void handleBounce(GolfBall golfBall, BlockFace blockFace) {
		golfBall.getUser().debug("on hit cannon block");
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
		return Set.of(Material.SMOKER);
	}
}
