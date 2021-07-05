package me.pugabyte.nexus.features.minigolf.models.blocks;

import me.pugabyte.nexus.features.minigolf.models.GolfBall;
import me.pugabyte.nexus.utils.MaterialTag;
import org.bukkit.Material;

import java.util.Set;

public class FrictionlessBlock extends ModifierBlock {
	@Override
	public void handle(GolfBall golfBall) {
		golfBall.getUser().debug("on frictionless block");

		golfBall.setVelocity(golfBall.getVelocity());
	}

	@Override
	public Set<Material> getMaterials() {
		return MaterialTag.ICE.getValues();
	}
}
