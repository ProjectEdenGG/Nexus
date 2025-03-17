package gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common;

import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlockUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface IRequireDirt extends ICustomTripwire {

	@Override
	default boolean canNotPlace(Block clickedBlock, Player player, BlockFace clickedFace, Block preBlock, Block underneath, ItemStack itemInHand) {
		CustomBlockUtils.debug(player, "&e- CustomBlock instance of IRequireDirt");
		if (!MaterialTag.DIRT.isTagged(underneath.getType())) {
			CustomBlockUtils.debug(player, "&c<- underneath (" + StringUtils.camelCase(preBlock.getType()) + ") is not a dirt type");
			return true;
		}

		return ICustomTripwire.super.canNotPlace(clickedBlock, player, clickedFace, preBlock, underneath, itemInHand);
	}
}
