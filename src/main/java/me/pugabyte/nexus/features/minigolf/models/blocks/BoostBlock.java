package me.pugabyte.nexus.features.minigolf.models.blocks;

import me.pugabyte.nexus.features.minigolf.models.GolfBall;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.Set;

public class BoostBlock extends ModifierBlock {
	@Override
	public void handle(GolfBall golfBall) {
		golfBall.getUser().debug("on boost block");

		Vector velocity = golfBall.getVelocity();
		if (golfBall.isNotMaxVelocity())
			golfBall.setVelocity(velocity.multiply(1.3));
	}

	@Override
	public Set<Material> getMaterials() {
		return Set.of(Material.REDSTONE_BLOCK);
	}
}
