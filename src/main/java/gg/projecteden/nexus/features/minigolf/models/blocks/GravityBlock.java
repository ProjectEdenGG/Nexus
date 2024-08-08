package gg.projecteden.nexus.features.minigolf.models.blocks;

import gg.projecteden.nexus.features.minigolf.models.GolfBall;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.Set;

public class GravityBlock extends ModifierBlock {

	@Override
	public ColorType getDebugDotColor() {
		return ColorType.GRAY;
	}

	@Override
	public void handleRoll(GolfBall golfBall, Block below) {
		// TODO: Utilize bounding box

		if (!golfBall.getSnowball().hasGravity()) {
			rollDebug(golfBall);

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
