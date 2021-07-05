package me.pugabyte.nexus.features.minigolf.models.blocks;

import me.pugabyte.nexus.features.minigolf.models.GolfBall;
import me.pugabyte.nexus.utils.MaterialTag;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

public class GravityBlock extends ModifierBlock {

	@Override
	public void handle(GolfBall golfBall) {
		golfBall.getUser().debug("on gravity block");

		// TODO: Utilize bounding box
		golfBall.getBall().setGravity(true);
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
