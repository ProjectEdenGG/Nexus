package gg.projecteden.nexus.features.minigolf.models.blocks;

import gg.projecteden.nexus.features.minigolf.models.GolfBall;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.Set;

public class FrictionBlock extends ModifierBlock {
	@Override
	public void handleRoll(GolfBall golfBall) {
		rollDebug(golfBall);

		Vector velocity = golfBall.getVelocity();
		velocity.multiply(0.9);
		golfBall.setVelocity(velocity);

		checkBallSpeed(golfBall, velocity);
	}

	@Override
	public Set<Material> getMaterials() {
		return Set.of(Material.SAND, Material.RED_SAND);
	}
}
