package gg.projecteden.nexus.features.minigolf.models.blocks;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.models.minigolf.GolfBall;
import gg.projecteden.nexus.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public abstract class ModifierSkull extends ModifierBlock {

	public int getSkullId() {
		return -1;
	}

	@Override
	public Set<Material> getMaterials() {
		return Set.of(Material.PLAYER_HEAD, Material.PLAYER_WALL_HEAD);
	}

	@Override
	public boolean additionalContext(Block block) {
		int id = getSkullId();
		if (id == -1) return true;

		ItemStack itemStack = ItemUtils.getItem(block);
		return String.valueOf(id).equals(Nexus.getHeadAPI().getItemID(itemStack));
	}

	@Override
	public void handleRoll(GolfBall golfBall, Block below) {
		super.handleRoll(golfBall, below);
	}

	@Override
	public void handleBounce(GolfBall golfBall, Block block, BlockFace blockFace) {
		super.handleBounce(golfBall, block, blockFace);
	}
}
