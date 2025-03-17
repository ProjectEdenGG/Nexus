package gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common;

import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlockUtils;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface IRequireSupport extends ICustomTripwire {

	@Override
	default boolean canNotPlace(Block clickedBlock, Player player, BlockFace clickedFace, Block preBlock, Block underneath, ItemStack itemInHand) {
		if (!(this instanceof IWaterLogged)) {
			CustomBlockUtils.debug(player, "&e- CustomBlock instance of IRequireSupport and not IWaterLogged");
			if (!underneath.isSolid()) {
				CustomBlockUtils.debug(player, "&c<- underneath (" + StringUtils.camelCase(preBlock.getType()) + ") is not solid");
				return true;
			}
		}

		return ICustomTripwire.super.canNotPlace(clickedBlock, player, clickedFace, preBlock, underneath, itemInHand);
	}
}
