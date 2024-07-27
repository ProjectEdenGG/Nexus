package gg.projecteden.nexus.features.minigolf.models.blocks;

import gg.projecteden.nexus.features.minigolf.models.GolfBall;
import gg.projecteden.nexus.utils.ColorType;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.Set;

public class BoostBlock extends ModifierBlock {

	@Override
	public ColorType getDebugDotColor() {
		return ColorType.PINK;
	}

	@Override
	public void handleRoll(GolfBall golfBall) {
		rollDebug(golfBall);

		Vector velocity = golfBall.getVelocity();
		if (golfBall.isNotMaxVelocity())
			golfBall.setVelocity(velocity.multiply(1.3));
	}

	@Override
	public Set<Material> getMaterials() {
		return Set.of(Material.REDSTONE_BLOCK);
	}
}
