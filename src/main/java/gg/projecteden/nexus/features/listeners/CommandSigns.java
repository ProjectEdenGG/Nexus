package gg.projecteden.nexus.features.listeners;

import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Arrays;
import java.util.List;

public class CommandSigns implements Listener {

	private static List<String> commands = Arrays.asList("[Disposal]", "[Trash]");

	@EventHandler
	public void onWriteSign(SignChangeEvent event) {
		String command = findCommand(event.getLines());
		if (command == null) return;

		int ndx = commandIndex(event.getLines(), command);
		if (ndx == -1) return;

		event.setLine(ndx, StringUtils.colorize("&1" + command));
	}

	private String findCommand(String[] lines) {
		for (String line : lines) {
			line = StringUtils.stripColor(line);
			if (commands.contains(line))
				return line;
		}
		return null;
	}

	private int commandIndex(String[] lines, String command) {
		command = StringUtils.stripColor(command);
		int ndx = -1;
		int count = 0;
		for (String line : lines) {
			line = StringUtils.stripColor(line);
			if (line.equalsIgnoreCase(command)) {
				ndx = count;
				break;
			}
			++count;
		}

		return ndx;
	}

	@EventHandler
	public void onClickOnSign(PlayerInteractEvent event) {
		if (!ActionGroup.CLICK_BLOCK.applies(event)) return;
		if (event.getClickedBlock() == null || !MaterialTag.SIGNS.isTagged(event.getClickedBlock().getType())) return;
		if (event.getHand() == null || !event.getHand().equals(EquipmentSlot.HAND)) return;

		Sign sign = (Sign) event.getClickedBlock().getState();

		String command = findCommand(sign.getLines());
		if (command == null) return;

		Player player = event.getPlayer();
		switch (command) {
			case "[Disposal]":
				event.setCancelled(true);
				disposal(player, sign);
				break;
			case "[Trash]":
				if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					event.setCancelled(true);
					trash(player);
				}
				break;
		}
	}

	private void disposal(Player player, Sign sign) {
		int ndx = commandIndex(sign.getLines(), "[Disposal]");
		sign.setLine(ndx, StringUtils.colorize("&1[Trash]"));
		sign.update();
		trash(player);
	}

	private void trash(Player player) {
		PlayerUtils.runCommand(player, "trash");
	}
}
