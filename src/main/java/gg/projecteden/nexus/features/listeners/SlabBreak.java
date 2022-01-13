package gg.projecteden.nexus.features.listeners;

import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Slab.Type;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class SlabBreak implements Listener {

	@EventHandler
	public void onSlabBreak(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		final Player player = event.getPlayer();
		final Material type = event.getBlock().getType();
		final Material handType = player.getInventory().getItemInMainHand().getType();

		if (!MaterialTag.SLABS.isTagged(type))
			return;
		if (!MaterialTag.SLABS.isTagged(handType))
			return;
		if (!Rank.of(player).isStaff())
			return;

		Slab data = (Slab) event.getBlock().getBlockData();
		Block target = player.getTargetBlockExact(10);
		if (data.getType() != Type.DOUBLE || target == null)
			return;

		Vector direction = player.getLocation().getDirection();
		Vector blockVector = null;
		for (double d = 1; d < 16; d += .06) {
			Vector multiplied = direction.clone().multiply(d).add(new Vector(0, player.getEyeHeight(), 0)).add(player.getLocation().toVector());
			Block foundBlock = multiplied.toLocation(player.getWorld()).getBlock();
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
		if (player.getGameMode() == GameMode.SURVIVAL)
			event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(event.getBlock().getType()));
	}

}
