package me.pugabyte.nexus.features.listeners;

import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.utils.MaterialTag;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
		if (!Rank.of(event.getPlayer()).isStaff()) return;

		Material type = event.getBlock().getType();
		Material handType = event.getPlayer().getInventory().getItemInMainHand().getType();
		if (MaterialTag.SLABS.isTagged(type) && MaterialTag.SLABS.isTagged(handType)) {
			Slab data = (Slab) event.getBlock().getBlockData();
			Block target = event.getPlayer().getTargetBlockExact(10);
			if (data.getType() != Type.DOUBLE || target == null)
				return;

			event.setCancelled(true);

			Vector direction = event.getPlayer().getLocation().getDirection();
			Vector blockVector = null;
			for (double d = 1; d < 16; d += .06) {
				Vector multiplied = direction.clone().multiply(d).add(new Vector(0, event.getPlayer().getEyeHeight(), 0)).add(event.getPlayer().getLocation().toVector());
				Block foundBlock = multiplied.toLocation(event.getPlayer().getWorld()).getBlock();
				if (foundBlock.getType() == type && foundBlock.getLocation().equals(target.getLocation())) {
					blockVector = multiplied;
					break;
				}
			}

			if (blockVector != null) {
				double blockY = blockVector.getY() - ((int) blockVector.getY());
				if (blockY > .5)
					data.setType(Type.BOTTOM);
				else
					data.setType(Type.TOP);
			} else
				data.setType(Type.BOTTOM);

			event.getBlock().setBlockData(data);
			if (event.getPlayer().getGameMode() == GameMode.SURVIVAL)
				event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(event.getBlock().getType()));
		}
	}

}
