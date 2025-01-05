package gg.projecteden.nexus.features.listeners;

import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignWarListener implements Listener {
	@EventHandler
	public void onClickOnSign(PlayerInteractEvent event) {
		if (!ActionGroup.CLICK_BLOCK.applies(event)) return;
		if (!isSignWarSign(event.getClickedBlock())) return;

		Sign sign = (Sign) event.getClickedBlock().getState();
		String number = StringUtils.stripColor(sign.getLine(2));
		if (!Utils.isLong(number)) return;

		long value = Long.parseLong(number);
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
			++value;
		else
			--value;

		sign.setLine(2, getColor(value) + value);
		sign.update();

		event.setCancelled(true);
	}

	@EventHandler
	public void onSignBreak(BlockBreakEvent event) {
		if (isSignWarSign(event.getBlock()))
			event.setCancelled(true);
	}

	public boolean isSignWarSign(Block block) {
		if (block == null) return false;
		if (!MaterialTag.SIGNS.isTagged(block.getType())) return false;
		if (block.getState() instanceof Sign)
			return ((Sign) block.getState()).getLine(0).equals(StringUtils.colorize("&1[Sign War]"));
		return false;
	}

	private String getColor(long value) {
		ColorType color = ColorType.BLACK;
		if (value > 0)
			color = ColorType.GREEN;
		else if (value < 0)
			color = ColorType.RED;
		return color.getChatColor().toString();
	}

}
