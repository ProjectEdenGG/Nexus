package gg.projecteden.nexus.features.minigolf.models.blocks;

import gg.projecteden.nexus.features.minigolf.models.GolfBall;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.Set;

public class DeathBlock extends ModifierBlock {

	@Override
	public void handleRoll(GolfBall golfBall) {
		if (!golfBall.isMinVelocity())
			golfBall.debug("&oon roll on death block");

		golfBall.respawn();
	}

	@Override
	public void handleBounce(GolfBall golfBall, Block block, BlockFace blockFace) {
		// ignore block face
		golfBall.debug("&oon hit death block, respawning...");
		golfBall.respawn();
	}

	@Override
	public Set<Material> getMaterials() {
		return Set.of(Material.WATER, Material.LAVA, Material.BARRIER, Material.CRIMSON_HYPHAE, Material.MAGMA_BLOCK);
	}
}
