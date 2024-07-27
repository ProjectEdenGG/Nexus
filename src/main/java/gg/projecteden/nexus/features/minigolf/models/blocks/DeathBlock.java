package gg.projecteden.nexus.features.minigolf.models.blocks;

import gg.projecteden.nexus.features.minigolf.models.GolfBall;
import gg.projecteden.nexus.utils.ColorType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.Set;

public class DeathBlock extends ModifierBlock {

	@Override
	public ColorType getDebugDotColor() {
		return ColorType.RED;
	}

	@Override
	public void handleRoll(GolfBall golfBall) {
		rollDebug(golfBall);

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
