package gg.projecteden.nexus.features.listeners;

import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/*
	Allows players to "fill" decorated pots with water, and "empty" them as well
	When a pot breaks, if the pot contains a water bucket, the water will be placed at the location of the pot
 */
public class DecoratedPot implements Listener {

	@EventHandler
	public void on(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if (Nullables.isNullOrAir(block))
			return;

		if (!(block.getState() instanceof org.bukkit.block.DecoratedPot decoratedPot))
			return;

		ItemStack tool = ItemUtils.getTool(event.getPlayer());
		if (Nullables.isNullOrAir(tool))
			return;

		ItemStack invItem = decoratedPot.getInventory().getItem();
		boolean containsItem = !Nullables.isNullOrAir(invItem);
		boolean containsWater = containsItem && invItem.getType() == Material.WATER_BUCKET;

		Material toolType = tool.getType();
		// Empty
		if (toolType == Material.BUCKET && containsWater) {
			event.setCancelled(true);
			decoratedPot.getInventory().setItem(null);
			tool.setType(Material.WATER_BUCKET);

			// Fill
		} else if (toolType == Material.WATER_BUCKET && !containsItem) {
			event.setCancelled(true);
			decoratedPot.getInventory().setItem(tool);
			tool.setType(Material.BUCKET);
		}
	}

	@EventHandler
	public void on(BlockBreakEvent event) {
		handle(event.getBlock());
	}

	@EventHandler
	public void on(EntityChangeBlockEvent event) {
		handle(event.getBlock());
	}

	private static void handle(Block block) {
		if (!(block.getState() instanceof org.bukkit.block.DecoratedPot decoratedPot))
			return;

		ItemStack item = decoratedPot.getInventory().getItem();
		if (Nullables.isNullOrAir(item))
			return;

		Material type = item.getType();
		if (type != Material.WATER_BUCKET)
			return;

		decoratedPot.getInventory().setItem(null);

		Tasks.wait(1, () -> block.setType(Material.WATER, true));
	}

}
