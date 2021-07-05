package me.pugabyte.nexus.features.minigolf.models.blocks;

import me.pugabyte.nexus.features.minigolf.models.GolfBall;
import org.bukkit.Material;

import java.util.Set;

public class BounceBlock extends ModifierBlock {

	@Override
	public void handle(GolfBall golfBall) {
		golfBall.getUser().debug("on bounce block");

		golfBall.setVelocity(golfBall.getVelocity().setY(0.30));
	}

	@Override
	public Set<Material> getMaterials() {
		return Set.of(Material.SLIME_BLOCK);
	}
}
