package gg.projecteden.nexus.features.blockmechanics.blocks;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.blockmechanics.BlockMechanicUtils;
import gg.projecteden.nexus.features.blockmechanics.events.SourcedBlockRedstoneEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class Netherrack implements Listener {

	public Netherrack() {
		Nexus.registerListener(this);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockRedstoneChangeFire(SourcedBlockRedstoneEvent event) {

		if (!BlockMechanicUtils.passesFilter(event, event.getBlock()))
			return;

		if (event.isMinor())
			return;

		if (event.getBlock().getType() != Material.NETHERRACK)
			return;

		Block above = event.getBlock().getRelative(0, 1, 0);

		if (event.isOn() && canReplaceWithFire(above.getType())) {
			above.setType(Material.FIRE);
		} else if (!event.isOn() && above != null && above.getType() == Material.FIRE) {
			above.setType(Material.AIR);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockRedstoneChangeSoulFire(SourcedBlockRedstoneEvent event) {

		if (!BlockMechanicUtils.passesFilter(event, event.getBlock()))
			return;

		if (event.isMinor())
			return;

		if (event.getBlock().getType() != Material.SOUL_SOIL)
			return;

		Block above = event.getBlock().getRelative(0, 1, 0);

		if (event.isOn() && canReplaceWithFire(above.getType())) {
			above.setType(Material.SOUL_FIRE);
		} else if (!event.isOn() && above != null && above.getType() == Material.SOUL_FIRE) {
			above.setType(Material.AIR);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onLeftClickFire(PlayerInteractEvent event) {

		if (!BlockMechanicUtils.passesFilter(event, event.getPlayer()))
			return;

		if (event.getAction() != Action.LEFT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND)
			return;

		if (event.getClickedBlock().getType() != Material.NETHERRACK)
			return;

		if (event.getBlockFace() == BlockFace.UP) {
			Block fire = event.getClickedBlock().getRelative(event.getBlockFace());
			if (fire.getType() == Material.FIRE && fire.getRelative(BlockFace.DOWN).isBlockPowered()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onLeftClickSoulFire(PlayerInteractEvent event) {

		if (!BlockMechanicUtils.passesFilter(event, event.getPlayer()))
			return;

		if (event.getAction() != Action.LEFT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND)
			return;

		if (event.getClickedBlock().getType() != Material.SOUL_SOIL)
			return;

		if (event.getBlockFace() == BlockFace.UP) {
			Block fire = event.getClickedBlock().getRelative(event.getBlockFace());
			if (fire.getType() == Material.SOUL_FIRE && fire.getRelative(BlockFace.DOWN).isBlockPowered()) {
				event.setCancelled(true);
			}
		}
	}

	private static boolean canReplaceWithFire(Material type) {
		return switch (type) {
			case SNOW, SHORT_GRASS, VINE, DEAD_BUSH, AIR -> true;
			default -> false;
		};
	}
}
