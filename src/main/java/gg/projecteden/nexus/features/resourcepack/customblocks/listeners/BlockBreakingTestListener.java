package gg.projecteden.nexus.features.resourcepack.customblocks.listeners;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.PlayerUtils;
import io.papermc.paper.event.block.BlockBreakProgressUpdateEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockDamageEvent;

import java.time.LocalTime;

@Disabled
public class BlockBreakingTestListener implements Listener {

	public BlockBreakingTestListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(BlockBreakProgressUpdateEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		PlayerUtils.send(player, "BlockBreakProgressUpdateEvent: " + LocalTime.now());
	}

	@EventHandler
	public void on(BlockDamageEvent event) {
		PlayerUtils.send(event.getPlayer(), "BlockDamageEvent: " + LocalTime.now());
	}

	@EventHandler
	public void on(BlockDamageAbortEvent event) {
		PlayerUtils.send(event.getPlayer(), "BlockDamageAbortEvent: " + LocalTime.now());
	}
}
