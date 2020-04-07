package me.pugabyte.bncore.features.listeners;

import me.pugabyte.bncore.utils.MaterialTag;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Slab.Type;
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
		if (MaterialTag.SLABS.isTagged(event.getBlock().getType())) {
			if (MaterialTag.SLABS.isTagged(event.getPlayer().getInventory().getItemInMainHand().getType())) {
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
						((Slab) event.getBlock().getBlockData()).setType(Type.BOTTOM);
						if (event.getPlayer().getGameMode() == GameMode.SURVIVAL)
							event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(event.getBlock().getType()));
					} else {
						((Slab) event.getBlock().getBlockData()).setType(Type.TOP);
						if (event.getPlayer().getGameMode() == GameMode.SURVIVAL)
							event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(event.getBlock().getType()));
					}
				} else {
					((Slab) event.getBlock().getBlockData()).setType(Type.BOTTOM);
					if (event.getPlayer().getGameMode() == GameMode.SURVIVAL)
						event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(event.getBlock().getType()));
				}
			}
		}
	}

}
