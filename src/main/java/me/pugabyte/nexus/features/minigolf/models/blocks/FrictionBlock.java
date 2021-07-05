package me.pugabyte.nexus.features.minigolf.models.blocks;

import me.pugabyte.nexus.features.minigolf.models.GolfBall;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.Set;

public class FrictionBlock extends ModifierBlock {
	@Override
	public void handle(GolfBall golfBall) {
		golfBall.getUser().debug("on friction block");

		Vector velocity = golfBall.getVelocity();
		velocity.multiply(0.9);
		golfBall.setVelocity(velocity);
	}

	@Override
	public Set<Material> getMaterials() {
		return Set.of(Material.SAND, Material.RED_SAND);
	}
}
