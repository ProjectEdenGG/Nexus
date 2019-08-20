package me.pugabyte.bncore.features.sideways.stairs;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.sideways.stairs.models.SidewaysStairsPlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import static me.pugabyte.bncore.features.sideways.stairs.SidewaysStairs.playerData;

/**
 * @author Camaros
 */
public class SidewaysStairsListener implements Listener {

	SidewaysStairsListener() {
		BNCore.registerListener(this);
	}

	@EventHandler
	public void onStairInteract(PlayerInteractEvent event) {

		if (event.getAction() == null || event.getHand() == null) return;
		if (event.getHand().equals(EquipmentSlot.HAND)) return;
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {

			Block block = event.getClickedBlock();
			String blockName = block.getType().toString();
			Player player = event.getPlayer();
			SidewaysStairsPlayer swsPlayer = playerData.get(player);

			if (swsPlayer != null && swsPlayer.isEnabled()) {
				if (swsPlayer.getAction().equals("copy")) {
					if (blockName.toLowerCase().endsWith("stairs")) {
						swsPlayer.setAction("set_angle");
						swsPlayer.setAngle(block.getData());
						swsPlayer.setEnabled(true);
						player.sendMessage(SidewaysStairs.PREFIX + "Angle succesfully copied (" + block.getData() + ")");
					} else {
						player.sendMessage(SidewaysStairs.PREFIX + "Can only copy angle of a stair block.");
					}
				}
			}
		}
	}

	@EventHandler
	public void onStairPlace(BlockPlaceEvent event) {

		Player player = event.getPlayer();
		Block block = event.getBlock();
		String blockName = block.getType().toString();
		SidewaysStairsPlayer swsPlayer = playerData.get(player);

		if (swsPlayer != null && swsPlayer.isEnabled()) {

			if (swsPlayer.getAction().equals("set_angle")) {
				if (blockName.toLowerCase().endsWith("stairs")) {
					block.setData(swsPlayer.getAngle());
				}
			} else if (swsPlayer.getAction().equals("disable_upsidedown_placement")) {
				if ((int) (block.getData()) > 3) {
					block.setData((byte) (block.getData() - 4));
				}
			}

		}

	}

}
