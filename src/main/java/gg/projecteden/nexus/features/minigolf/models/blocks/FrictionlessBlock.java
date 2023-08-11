package gg.projecteden.nexus.features.minigolf.models.blocks;

import gg.projecteden.nexus.features.minigolf.models.GolfBall;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.Material;

import java.util.Set;

public class FrictionlessBlock extends ModifierBlock {
	@Override
	public void handleRoll(GolfBall golfBall) {
		if (!golfBall.isMinVelocity())
			golfBall.getUser().debug("&oon roll on frictionless block");

		golfBall.setVelocity(golfBall.getVelocity());
	}

	@Override
	public Set<Material> getMaterials() {
		return MaterialTag.ICE.getValues();
	}
}
