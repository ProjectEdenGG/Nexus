package gg.projecteden.nexus.features.resourcepack.customblocks.models.common;

import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlockUtils;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.ICustomTripwire;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import java.util.ArrayList;

public interface IPistonActions extends Listener {

	PistonAction getPistonPushAction();

	PistonAction getPistonPullAction();

	enum PistonAction {
		MOVE,
		PREVENT,
		BREAK;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	default void onPush(BlockPistonExtendEvent event) {
		if (event.isCancelled())
			return;

		for (Block block : new ArrayList<>(event.getBlocks())) {
			// TODO: Disable tripwire customblocks
			if (ICustomTripwire.isNotEnabled() && block.getType() == Material.TRIPWIRE)
				continue;
			//

			CustomBlock customBlock = CustomBlock.from(block);
			if (customBlock == null)
				continue;

			ICustomBlock iCustomBlock = customBlock.get();
			PistonAction pistonAction = iCustomBlock.getPistonPushAction();
			switch (pistonAction) {
				case PREVENT -> {
					CustomBlockUtils.broadcastDebug("PistonEvent: " + customBlock.name() + " cannot be moved by pistons");
					event.setCancelled(true);
					return;
				}
				case BREAK -> {
					CustomBlockUtils.broadcastDebug("PistonEvent: " + customBlock.name() + " broke because of a piston");
					CustomBlockUtils.breakBlock(block, customBlock, null, null, true);
					// TODO: REMOVE THIS BLOCK FROM THE BLOCKS THAT ARE MOVING, AND ANY BLOCKS "PAST" THIS BLOCK SHOULD BE REMOVED AS WELL --> PARCHMENT?
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	default void onRetract(BlockPistonRetractEvent event) {
		if (event.isCancelled())
			return;

		for (Block block : new ArrayList<>(event.getBlocks())) {
			// TODO: Disable tripwire customblocks
			if (ICustomTripwire.isNotEnabled() && block.getType() == Material.TRIPWIRE)
				continue;
			//

			CustomBlock customBlock = CustomBlock.from(block);
			if (customBlock == null)
				continue;

			ICustomBlock iCustomBlock = customBlock.get();
			PistonAction pistonAction = iCustomBlock.getPistonPullAction();
			switch (pistonAction) {
				case PREVENT -> {
					CustomBlockUtils.broadcastDebug("PistonEvent: " + customBlock.name() + " cannot be moved by pistons");
					event.setCancelled(true);
					return;
				}
				case BREAK -> {
					CustomBlockUtils.broadcastDebug("PistonEvent: " + customBlock.name() + " broke because of a piston");
					CustomBlockUtils.breakBlock(block, customBlock, null, null, true);
					// TODO: REMOVE THIS BLOCK FROM THE BLOCKS THAT ARE MOVING, AND ANY BLOCKS "PAST" THIS BLOCK SHOULD BE REMOVED AS WELL --> PARCHMENT?
				}
			}
		}
	}
}
