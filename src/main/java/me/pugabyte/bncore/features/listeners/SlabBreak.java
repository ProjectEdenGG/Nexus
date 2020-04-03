package me.pugabyte.bncore.features.listeners;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class SlabBreak implements Listener {

	@EventHandler
	public void onSlabBreak(BlockBreakEvent event) {
		if (event.isCancelled()) return;
		if (!event.getPlayer().hasPermission("group.staff")) return;
		if (isDoubleSlab(event.getBlock())) {
			if (event.getPlayer().getInventory().getItemInMainHand().getType().name().contains("slab")) {
				event.setCancelled(true);

				Vector direction = event.getPlayer().getLocation().getDirection();
				Vector blockVector = null;
				for (double d = 1; d < 16; d += .06) {
					Vector multiplied = direction.clone().multiply(d).add(new Vector(0, event.getPlayer().getEyeHeight(), 0)).add(event.getPlayer().getLocation().toVector());
					Location multipliedLocation = multiplied.toLocation(event.getPlayer().getWorld());
					if (multipliedLocation.getBlock().getType() == event.getBlock().getType()) {
						blockVector = multiplied;
						break;
					}
				}

				if (blockVector != null) {
					double blockY = blockVector.getY() - ((int) blockVector.getY());
					if (blockY > .5) {
						event.getBlock().setTypeIdAndData(event.getBlock().getTypeId() + 1, event.getBlock().getData(), true);
						if (event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
							event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(event.getBlock().getType(), 1, event.getBlock().getData()));
						}
					} else {
						event.getBlock().setTypeIdAndData(event.getBlock().getTypeId() + 1, (byte) (event.getBlock().getData() + 8), true);
						if (event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
							event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(event.getBlock().getType(), 1, (byte) (event.getBlock().getData() & 7)));
						}
					}
				} else {
					event.getBlock().setTypeIdAndData(event.getBlock().getTypeId() + 1, event.getBlock().getData(), true);
					if (event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
						event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(event.getBlock().getType(), 1, event.getBlock().getData()));
					}
				}
			}
		}
	}

	boolean isDoubleSlab(Block block) {
		if (block == null) {
			return false;
		}
		switch (block.getType()) {
			case DOUBLE_STEP:
			case WOOD_DOUBLE_STEP:
			case PURPUR_DOUBLE_SLAB:
			case DOUBLE_STONE_SLAB2:
				return true;
		}
		return false;
	}


}
