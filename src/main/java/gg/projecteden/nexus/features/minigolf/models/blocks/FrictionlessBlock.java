package gg.projecteden.nexus.features.minigolf.models.blocks;

import gg.projecteden.nexus.features.minigolf.models.GolfBall;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.Set;

public class FrictionlessBlock extends ModifierBlock {

	@Override
	public ColorType getDebugDotColor() {
		return ColorType.LIGHT_BLUE;
	}

	@Override
	public void handleRoll(GolfBall golfBall) {
		rollDebug(golfBall);

		Vector velocity = golfBall.getVelocity();
		golfBall.setVelocity(golfBall.getVelocity());

		checkBallSpeed(golfBall, velocity);
	}

	@Override
	public Set<Material> getMaterials() {
		return MaterialTag.ICE.getValues();
	}
}
