package gg.projecteden.nexus.features.resourcepack.customblocks.models.common;

import gg.projecteden.nexus.features.listeners.Composter;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.utils.Nullables;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface ICompostable extends ICustomBlock {

	default boolean compost(ItemStack itemStack, Block block) {
		return compost(null, itemStack, block);
	}

	default boolean compost(@Nullable Player player, ItemStack itemStack, Block block) {
		return Composter.compostItem(player, itemStack, block);
	}

	@Override
	default boolean onUseWhileHolding(PlayerInteractEvent event, Player player, Action action, Block clickedBlock, ItemStack itemInHand) {
		if (compost(player, itemInHand, clickedBlock)) {
			event.setCancelled(true);
			return true;
		}

		return false;
	}

	@EventHandler
	default void on(InventoryMoveItemEvent event) {
		if (!(event.getDestination().getHolder() instanceof BlockInventoryHolder holder))
			return;

		Block block = holder.getBlock();
		if (Nullables.isNullOrAir(block))
			return;

		ItemStack item = event.getItem();
		if (Nullables.isNullOrAir(item))
			return;

		CustomBlock customBlock = CustomBlock.from(item);
		if (customBlock == null)
			return;

		if (!(customBlock.get() instanceof ICompostable compostable))
			return;

		compostable.compost(item, block);
	}
}
