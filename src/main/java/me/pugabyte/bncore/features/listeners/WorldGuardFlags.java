package me.pugabyte.bncore.features.listeners;

import com.sk89q.worldguard.protection.flags.StateFlag.State;
import me.pugabyte.bncore.utils.WorldGuardFlagUtils;
import me.pugabyte.bncore.utils.WorldGuardFlagUtils.Flags;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakEvent;

public class WorldGuardFlags implements Listener {

	@EventHandler
	public void onItemFrameBreak(HangingBreakEvent event) {
		if (WorldGuardFlagUtils.query(event.getEntity().getLocation(), Flags.HANGING_BREAK) == State.DENY)
			event.setCancelled(true);
	}

}
