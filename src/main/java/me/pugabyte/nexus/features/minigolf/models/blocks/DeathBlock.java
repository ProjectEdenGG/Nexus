package me.pugabyte.nexus.features.minigolf.models.blocks;

import me.pugabyte.nexus.features.minigolf.MiniGolfUtils;
import me.pugabyte.nexus.features.minigolf.models.GolfBall;
import org.bukkit.Material;

import java.util.Set;

public class DeathBlock extends ModifierBlock {

	@Override
	public void handle(GolfBall golfBall) {
		golfBall.getUser().debug("on death block");
		MiniGolfUtils.respawnBall(golfBall);
	}

	@Override
	public Set<Material> getMaterials() {
		return Set.of(Material.WATER, Material.LAVA, Material.BARRIER);
	}
}
