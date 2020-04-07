package me.pugabyte.bncore.features.listeners;

import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;

import static me.pugabyte.bncore.utils.StringUtils.colorize;
import static me.pugabyte.bncore.utils.StringUtils.stripColor;

public class SignWarListener implements Listener {
	@EventHandler
	public void onClickOnSign(PlayerInteractEvent event) {
		if (!Arrays.asList(Action.RIGHT_CLICK_BLOCK, Action.LEFT_CLICK_BLOCK).contains(event.getAction())) return;
		if (!isSignWarSign(event.getClickedBlock())) return;

		Sign sign = (Sign) event.getClickedBlock().getState();
		String number = stripColor(sign.getLine(2));
		if (!Utils.isInt(number)) return;

		int value = Integer.parseInt(number);
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
			++value;
		else
			--value;

		sign.setLine(2, getColor(value) + value);
		sign.update();
	}

	@EventHandler
	public void onSignBreak(BlockBreakEvent event) {
		if (isSignWarSign(event.getBlock()))
			event.setCancelled(true);
	}

	public boolean isSignWarSign(Block block) {
		if (!MaterialTag.SIGNS.isTagged(block.getType())) return false;
		if (block.getState() instanceof Sign)
			return ((Sign) block.getState()).getLine(0).equals(colorize("&1[Sign War]"));
		return false;
	}

	private String getColor(int value) {
		ColorType color = ColorType.BLACK;
		if (value > 0)
			color = ColorType.GREEN;
		else if (value < 0)
			color = ColorType.RED;
		return color.getChatColor().toString();
	}

}
