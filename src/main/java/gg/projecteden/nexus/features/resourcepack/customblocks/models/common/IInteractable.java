package gg.projecteden.nexus.features.resourcepack.customblocks.models.common;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public interface IInteractable {

	default boolean onUseWhileHolding(PlayerInteractEvent event, Player player, Action action, Block clickedBlock, ItemStack itemInHand, EquipmentSlot hand) {
		return false;
	}

	default boolean onRightClickedWithItem(Player player, CustomBlock customBlock, Block block, BlockFace face, ItemStack itemInHand) {
		return false;
	}

	default boolean onRightClickedWithoutItem(Player player, CustomBlock customBlock, Block block, BlockFace face) {
		return false;
	}

	default boolean onLeftClickedWithItem(Player player, CustomBlock customBlock, Block block, BlockFace face, ItemStack itemInHand) {
		return false;
	}

	default boolean onLeftClickedWithoutItem(Player player, CustomBlock customBlock, Block block, BlockFace face) {
		return false;
	}
}
