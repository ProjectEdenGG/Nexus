package gg.projecteden.nexus.features.minigolf.models.blocks;

import gg.projecteden.nexus.features.minigolf.models.GolfBall;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

public class GravityBlock extends ModifierBlock {

	@Override
	public void handleRoll(GolfBall golfBall) {
		// TODO: Utilize bounding box

		if (!golfBall.getSnowball().hasGravity()) {
			if (!golfBall.isMinVelocity())
				golfBall.getUser().debug("&oon roll on gravity block");

			golfBall.getSnowball().setGravity(true);
		}
	}

	@Override
	public Set<Material> getMaterials() {
		return new HashSet<>() {{
			addAll(MaterialTag.ALL_AIR.getValues());
			add(Material.VINE);
			add(Material.LADDER);
		}};
	}
}
