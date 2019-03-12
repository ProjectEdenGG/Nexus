package me.pugabyte.bncore.features.sideways.logs;

import me.pugabyte.bncore.BNCore;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import static me.pugabyte.bncore.features.sideways.logs.SidewaysLogs.enabledPlayers;

public class SidewaysLogsListener implements Listener {

	SidewaysLogsListener() {
		BNCore.registerListener(this);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Block block = event.getBlockPlaced();
		Player player = event.getPlayer();

		if (!(block.getType() == Material.OAK_LOG ||
				block.getType() == Material.SPRUCE_LOG ||
				block.getType() == Material.BIRCH_LOG ||
				block.getType() == Material.JUNGLE_LOG ||
				block.getType() == Material.ACACIA_LOG ||
				block.getType() == Material.DARK_OAK_LOG
		)) return;

		if (!enabledPlayers.contains(player)) return;

//		block.setData((byte) (block.getData() % 4));
	}

}
