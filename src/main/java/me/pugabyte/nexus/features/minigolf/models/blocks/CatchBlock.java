package me.pugabyte.nexus.features.minigolf.models.blocks;

import me.pugabyte.nexus.features.minigolf.models.GolfBall;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.Set;

public class CatchBlock extends ModifierBlock {
	@Override
	public void handle(GolfBall golfBall) {
		golfBall.getUser().debug("on cannon block");

		Vector velocity = golfBall.getVelocity();
		velocity.setY(0);
		golfBall.setVelocity(velocity);
	}

	@Override
	public Set<Material> getMaterials() {
		return Set.of(Material.SOUL_SOIL);
	}
}
