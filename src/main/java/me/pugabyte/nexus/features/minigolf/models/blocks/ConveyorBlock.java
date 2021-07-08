package me.pugabyte.nexus.features.minigolf.models.blocks;

import me.pugabyte.nexus.features.minigolf.MiniGolfUtils;
import me.pugabyte.nexus.features.minigolf.models.GolfBall;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional;
import org.bukkit.util.Vector;

import java.util.Set;

public class ConveyorBlock extends ModifierBlock {
	@Override
	public void handleRoll(GolfBall golfBall) {
		golfBall.getUser().debug("&oon roll on conveyor block");

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
