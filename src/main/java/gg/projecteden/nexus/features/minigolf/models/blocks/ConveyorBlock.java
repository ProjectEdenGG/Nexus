package gg.projecteden.nexus.features.minigolf.models.blocks;

import gg.projecteden.nexus.features.minigolf.MiniGolfUtils;
import gg.projecteden.nexus.features.minigolf.models.GolfBall;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional;
import org.bukkit.util.Vector;

import java.util.Set;

// TODO: Apply some velocity on bounce as well?
public class ConveyorBlock extends ModifierBlock {
	@Override
	public void handleRoll(GolfBall golfBall) {
		rollDebug(golfBall);

		Vector velocity = golfBall.getVelocity();
		Block below = golfBall.getBlockBelow();

		if (!(below.getBlockData() instanceof Directional directional))
			return;

		Vector direction = MiniGolfUtils.getDirection(directional.getFacing(), 0.1);
		if (direction == null)
			return;

		// Push ball
		golfBall.setVelocity(velocity.multiply(9.0).add(direction).multiply(0.1));
	}

	@Override
	public Set<Material> getMaterials() {
		return Set.of(Material.MAGENTA_GLAZED_TERRACOTTA);
	}
}
