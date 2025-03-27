package gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.incremental;

import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlockUtils;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.ICustomTripwire;
import gg.projecteden.nexus.utils.GameModeWrapper;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface IIncremental extends ICustomTripwire {

	List<String> getModelIdList();

	default int getIndex() {
		return getModelIdList().indexOf(getModel());
	}

	@Override
	default boolean onRightClickedWithItem(Player player, CustomBlock customBlock, Block block, BlockFace face, ItemStack itemInHand) {
		if (player.isSneaking())
			return false;

		CustomBlock customBlockItem = CustomBlock.from(itemInHand);
		if (customBlockItem == null)
			return false;

		List<String> modelIdList = this.getModelIdList();
		if (!modelIdList.contains(customBlockItem.get().getModel()))
			return false;

		int ndx = this.getIndex() + 1;
		if (ndx >= modelIdList.size())
			return false;

		String newModelId = modelIdList.get(ndx);
		CustomBlock update = CustomBlock.from(newModelId);
		if (update == null)
			return false;

		player.swingMainHand();
		customBlock.updateBlock(player, update, block);
		if (!GameModeWrapper.of(player).isCreative())
			itemInHand.subtract();

		CustomBlockUtils.debug(player, "&a<- incremented block");
		return true;
	}
}
