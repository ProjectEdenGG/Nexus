package gg.projecteden.nexus.features.resourcepack.customblocks.models.common;

import gg.projecteden.nexus.features.listeners.Composter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface ICompostable extends ICustomBlock {

	int getCompostChance();

	default boolean compost(ItemStack itemStack, Block block) {
		return compost(null, itemStack, block);
	}

	default boolean compost(@Nullable Player player, ItemStack itemStack, Block block) {
		return Composter.compostItem(player, itemStack, block, getCompostChance());
	}

	@Override
	default boolean onUseWhileHolding(PlayerInteractEvent event, Player player, Action action, Block clickedBlock, ItemStack itemInHand) {
		if (compost(player, itemInHand, clickedBlock)) {
			event.setCancelled(true);
			return true;
		}

		return false;
	}
}
