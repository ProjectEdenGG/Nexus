package gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.tall;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.IRequireDirt;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ITallFlower extends ITall, IRequireDirt {

	@Override
	default boolean canNotPlace(Block clickedBlock, Player player, BlockFace clickedFace, Block preBlock, Block underneath, ItemStack itemInHand) {
		return ITall.super.canNotPlace(clickedBlock, player, clickedFace, preBlock, underneath, itemInHand)
			|| IRequireDirt.super.canNotPlace(clickedBlock, player, clickedFace, preBlock, underneath, itemInHand);
	}
}
