package gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.tall;

import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlockUtils;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.IRequireSupport;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.IWaterLogged;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ITall extends IRequireSupport {
	@Override
	default boolean requiresCorrectToolForDrops() {
		return false;
	}

	@Override
	default boolean canNotPlace(Block clickedBlock, Player player, BlockFace clickedFace, Block preBlock, Block underneath, ItemStack itemInHand) {
		CustomBlockUtils.debug(player, "&e- CustomBlock instance of IWaterLogged");
		Block above = preBlock.getRelative(BlockFace.UP);

		boolean placeTallSupport = false;
		if (!(this instanceof IWaterLogged))
			placeTallSupport = true;
		else if (clickedBlock.getType() != Material.WATER)
			placeTallSupport = true;

		if (placeTallSupport && !Nullables.isNullOrAir(above)) {
			CustomBlockUtils.debug(player, "&c<- above (" + StringUtils.camelCase(preBlock.getType()) + ") is not null/air");
			return true;
		}

		return IRequireSupport.super.canNotPlace(clickedBlock, player, clickedFace, preBlock, underneath, itemInHand);
	}
}
