package gg.projecteden.nexus.features.blockmechanics.mechanics;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.blockmechanics.BlockMechanicUtils;
import gg.projecteden.nexus.features.listeners.events.SourcedBlockRedstoneEvent;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

// When powering Netherrack & Soul Sand, fire is placed on top of the block if it can
public class BlockFireToggle implements Listener {

	public BlockFireToggle() {
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

	private static final List<Material> disallowedMaterials = List.of(Material.LAVA, Material.WATER,
		Material.BUBBLE_COLUMN, Material.LIGHT);

	private static boolean canReplaceWithFire(Material type) {
		if (disallowedMaterials.contains(type))
			return false;

		return MaterialTag.REPLACEABLE.getValues().contains(type);
	}
}
