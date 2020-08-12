package me.pugabyte.bncore.features.shops;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;

import static me.pugabyte.bncore.utils.StringUtils.stripColor;

public class ShopDisabler implements Listener {

	public ShopDisabler() {
		BNCore.registerListener(this);
	}

	public boolean isShopSign(Block block) {
		if (block == null) return false;
		if (!MaterialTag.SIGNS.isTagged(block.getType())) return false;

		Sign sign = (Sign) block.getState();
		return Arrays.asList("[Trade]", "[Arrow Trade]", "[Potion Trade]", "[Ench Trade]").contains(stripColor(sign.getLine(0)));
	}

	public void tell(Player player) {
		Utils.send(player, StringUtils.getPrefix("Shops") + "Shop signs are temporarily disabled until the new shop system is in place.");
	}

	@EventHandler
	public void onSignClick(PlayerInteractEvent event) {
		if (!Arrays.asList(Action.RIGHT_CLICK_BLOCK, Action.LEFT_CLICK_BLOCK).contains(event.getAction())) return;
		if (!isShopSign(event.getClickedBlock())) return;

		event.setCancelled(true);
		tell(event.getPlayer());
	}

	@EventHandler
	public void onBreakSign(BlockBreakEvent event) {
		if (!isShopSign(event.getBlock())) return;

		event.setCancelled(true);
		tell(event.getPlayer());
	}

	@EventHandler
	public void onBreakSignSupport(BlockBreakEvent event) {
		Block up = event.getBlock().getRelative(BlockFace.UP);
		if (isShopSign(up) && MaterialTag.STANDING_SIGNS.isTagged(up.getType())) {
			event.setCancelled(true);
			tell(event.getPlayer());
			return;
		}

		for (BlockFace blockFace : Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST)) {
			Block relative = event.getBlock().getRelative(blockFace);
			if (!MaterialTag.WALL_SIGNS.isTagged(relative.getType()))
				continue;

			WallSign data = (WallSign) relative.getBlockData();
			if (data.getFacing() == blockFace.getOppositeFace())
				continue;

			if (!isShopSign(relative))
				continue;

			event.setCancelled(true);
			tell(event.getPlayer());
			return;
		}
	}
}
