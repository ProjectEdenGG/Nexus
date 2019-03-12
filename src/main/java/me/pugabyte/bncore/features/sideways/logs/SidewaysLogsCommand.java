package me.pugabyte.bncore.features.sideways.logs;

import me.pugabyte.bncore.BNCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.pugabyte.bncore.features.sideways.logs.SidewaysLogs.enabledPlayers;

public class SidewaysLogsCommand implements CommandExecutor {
	private final static String PREFIX = BNCore.getPrefix("SidewaysLogs");

	SidewaysLogsCommand() {
		BNCore.registerCommand("sidewayslogs", this);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You must be in-game to run this command");
			return true;
		}

		Player player = (Player) sender;

		if (enabledPlayers.contains(player)) {
			enabledPlayers.remove(player);
			sender.sendMessage(PREFIX + "Now placing logs normally");
		} else {
			enabledPlayers.add(player);
			sender.sendMessage(PREFIX + "Now placing logs vertically");
		}
		return true;
	}
}
