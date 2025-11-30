package gg.projecteden.nexus.features.minigolf.models.blocks;

import gg.projecteden.nexus.features.minigolf.MiniGolfUtils;
import gg.projecteden.nexus.models.minigolf.GolfBall;
import gg.projecteden.nexus.utils.ColorType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional;
import org.bukkit.util.Vector;

import java.util.Set;

public class DirectionalBoostBlock extends ModifierBlock {

	@Override
	public ColorType getDebugDotColor() {
		return ColorType.PINK;
	}

	@Override
	public void handleRoll(GolfBall golfBall, Block below) {
		rollDebug(golfBall);

		Vector velocity = golfBall.getVelocity();

		if (!(below.getBlockData() instanceof Directional directional))
			return;

		// Get Direction
		Vector direction = MiniGolfUtils.getDirection(directional.getFacing(), 0.5);
		if (direction == null)
			return;

		if (golfBall.isNotMaxVelocity()) {
			// Push ball
			golfBall.setVelocity(velocity.multiply(9.3).add(direction).multiply(0.1));
		}
	}

	@Override
	public Set<Material> getMaterials() {
		return Set.of(Material.OBSERVER);
	}
}
