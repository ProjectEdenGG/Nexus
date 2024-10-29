package gg.projecteden.nexus.features.blockmechanics;

import gg.projecteden.nexus.features.listeners.events.fake.FakeBlockBreakEvent;
import gg.projecteden.nexus.features.listeners.events.fake.FakeBlockPlaceEvent;
import gg.projecteden.nexus.features.listeners.events.fake.FakeEvent;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class BlockMechanicUtils {

	public static boolean INDIRECT_REDSTONE = false;
	public static boolean ADVANCED_BLOCK_CHECKS = true;
	public static boolean PENDANTIC_BLOCK_CHECKS = true;

	public static boolean passesFilter(Event event) {
		if (event instanceof FakeEvent || event instanceof com.gmail.nossr50.events.fake.FakeEvent)
			return false;

		if (ADVANCED_BLOCK_CHECKS) {
			if (event instanceof Cancellable && ((Cancellable) event).isCancelled())
				return event instanceof PlayerInteractEvent && ((PlayerInteractEvent) event).getClickedBlock() == null;
		}

		return true;
	}

	public static boolean canBuild(Player player, Location loc, boolean build) {
		return canBuild(player, loc.getBlock(), build);
	}

	public static boolean canBuild(Player player, Block block, boolean build) {
		if (ADVANCED_BLOCK_CHECKS) {
			BlockEvent event;
			if (build)
				event = new FakeBlockPlaceEvent(block, block.getState(), block.getRelative(0, -1, 0), player.getInventory().getItemInMainHand(), player, true, EquipmentSlot.HAND);
			else
				event = new FakeBlockBreakEvent(block, player);

			event.callEvent();

			return !(((Cancellable) event).isCancelled() || event instanceof BlockPlaceEvent && !((BlockPlaceEvent) event).canBuild());
		}

		if (WorldGuardUtils.plugin == null)
			return false;

		if (build)
			return WorldGuardUtils.plugin.createProtectionQuery().testBlockPlace(player, block.getLocation(), block.getType());
		else
			return WorldGuardUtils.plugin.createProtectionQuery().testBlockBreak(player, block);

	}
}
