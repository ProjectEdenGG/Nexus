package gg.projecteden.nexus.features.minigolf.models.blocks;

import gg.projecteden.nexus.features.minigolf.models.GolfBall;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.SoundBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.Set;

public class BounceBlock extends ModifierBlock {
	public static final double BOUNCE_Y = 0.40; // 0.30
	public static final double BOUNCE_XZ = 0.35; // 0.25

	@Override
	public ColorType getDebugDotColor() {
		return ColorType.LIGHT_GREEN;
	}

	@Override
	public void handleRoll(GolfBall golfBall, Block below) {
		rollDebug(golfBall);

		new SoundBuilder(Sound.BLOCK_SLIME_BLOCK_HIT).location(golfBall.getLocation()).volume(0.5).play();

		golfBall.setVelocity(golfBall.getVelocity().setY(BOUNCE_Y));
	}

	@Override
	public void handleBounce(GolfBall golfBall, Block block, BlockFace blockFace) {
		golfBall.debug("&oon hit bounce block");

		new SoundBuilder(Sound.BLOCK_SLIME_BLOCK_HIT).location(golfBall.getLocation()).volume(0.5).play();

		Vector velocity = golfBall.getVelocity();
		switch (blockFace) {
			case NORTH, SOUTH -> velocity.setZ(Math.copySign(BOUNCE_XZ, -velocity.getZ()));
			case EAST, WEST -> velocity.setX(Math.copySign(BOUNCE_XZ, -velocity.getX()));
			case UP -> velocity.setY(BOUNCE_Y);
			case DOWN -> velocity.setY(-BOUNCE_Y);
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
