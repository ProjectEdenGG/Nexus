package me.pugabyte.nexus.features.minigolf.models.blocks;

import me.pugabyte.nexus.features.minigolf.MiniGolfUtils;
import me.pugabyte.nexus.features.minigolf.models.GolfBall;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.Set;

public class DeathBlock extends ModifierBlock {

	@Override
	public void handleRoll(GolfBall golfBall) {
		golfBall.getUser().debug("&oon roll on death block");
		MiniGolfUtils.respawnBall(golfBall);
	}

	@Override
	public void handleBounce(GolfBall golfBall, BlockFace blockFace) {
		// ignore block face
		golfBall.debug("&oon hit death block, respawning...");
		golfBall.respawn();
	}

	@Override
	public Set<Material> getMaterials() {
		return Set.of(Material.WATER, Material.LAVA, Material.BARRIER);
	}
}
