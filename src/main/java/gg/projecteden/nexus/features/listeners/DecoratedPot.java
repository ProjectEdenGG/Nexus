package gg.projecteden.nexus.features.listeners;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.LocationUtils.CardinalDirection;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.DecoratedPot.Side;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class DecoratedPot implements Listener {

	/*
		Allows players to change decorated pot designs by crouch clicking the pot with the sherd
	 */
	@EventHandler
	public void onPotInteractWithSherd(PlayerInteractEvent event) {
		var block = event.getClickedBlock();
		var sherd = event.getItem();
		var player = event.getPlayer();

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		if (isNullOrAir(block))
			return;

		if (!(block.getBlockData() instanceof Directional directional))
			return;

		if (!(block.getState() instanceof org.bukkit.block.DecoratedPot pot))
			return;

		if (!MaterialTag.POTTERY_SHERDS.isTagged(sherd))
			return;

		if (!player.isSneaking())
			return;

		if (!Rank.of(player).isStaff())
			return;

		if (player.getGameMode() != GameMode.CREATIVE)
			return;

		var blockFace = event.getBlockFace();
		var facing = directional.getFacing().getOppositeFace();

		Side side = null;
		if (facing == blockFace)
			side = Side.FRONT;

		if (CardinalDirection.of(facing).turnLeft().toBlockFace() == blockFace)
			side = Side.RIGHT;

		if (CardinalDirection.of(facing).turnRight().toBlockFace() == blockFace)
			side = Side.LEFT;

		if (facing.getOppositeFace() == blockFace)
			side = Side.BACK;

		if (side == null)
			return;

		if (pot.getSherd(side).equals(sherd.getType()))
			pot.setSherd(side, null);
		else
			pot.setSherd(side, sherd.getType());

		event.setCancelled(true);
		pot.update(true);
	}

	/*
		Allows players to "fill" decorated pots with water, and "empty" them as well
		When a pot breaks, if the pot contains a water bucket, the water will be placed at the location of the pot
 	*/
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
		// Empty Pot
		if (toolType == Material.BUCKET && containsWater) {
			event.setCancelled(true);
			decoratedPot.getInventory().setItem(null);
			tool.setType(Material.WATER_BUCKET);
			new SoundBuilder(Sound.ITEM_BUCKET_FILL).location(block).volume(0.5).pitch(0.7).play();
			new ParticleBuilder(Particle.REDSTONE).location(block.getLocation().clone().add(0.5, 1.2, 0.5)).count(7).color(Color.WHITE).extra(0).spawn();

			// Fill Pot
		} else if (toolType == Material.WATER_BUCKET && !containsItem) {
			event.setCancelled(true);
			decoratedPot.getInventory().setItem(tool);
			tool.setType(Material.BUCKET);

			new SoundBuilder(Sound.BLOCK_DECORATED_POT_INSERT).location(block).volume(0.5).pitch(0.7).play();
			new ParticleBuilder(Particle.REDSTONE).location(block.getLocation().clone().add(0.5, 1.2, 0.5)).count(7).color(Color.WHITE).extra(0).spawn();
		}
	}

	@EventHandler
	public void on(BlockBreakEvent event) {
		handleBreak(event.getBlock());
	}

	@EventHandler
	public void on(EntityChangeBlockEvent event) {
		handleBreak(event.getBlock());
	}

	private static void handleBreak(Block block) {
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
