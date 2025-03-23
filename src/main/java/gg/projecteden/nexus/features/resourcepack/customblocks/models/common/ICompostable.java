package gg.projecteden.nexus.features.resourcepack.customblocks.models.common;

import gg.projecteden.nexus.features.listeners.Composter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface ICompostable extends ICustomBlock {

	int getCompostChance();

	default boolean compost(ItemStack itemStack, Block block, @Nullable EquipmentSlot hand) {
		return compost(null, itemStack, block, hand);
	}

	default boolean compost(@Nullable Player player, ItemStack itemStack, Block block, @Nullable EquipmentSlot hand) {
		return Composter.compostItem(player, itemStack, block, getCompostChance(), hand);
	}

	@Override
	default boolean onUseWhileHolding(PlayerInteractEvent event, Player player, Action action, Block clickedBlock, ItemStack itemInHand, @Nullable EquipmentSlot hand) {
		if (compost(player, itemInHand, clickedBlock, hand)) {
			event.setCancelled(true);
			return true;
		}

		return false;
	}
}
