package me.pugabyte.bncore.features.scoreboards;

import fr.minuskube.netherboard.Netherboard;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import me.pugabyte.bncore.BNCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

import static me.pugabyte.bncore.BNCore.colorize;

public class ScoreboardsCommand implements CommandExecutor, TabCompleter {

	public ScoreboardsCommand() {
		BNCore.registerCommand("scoreboards", this);
		BNCore.registerTabCompleter("scoreboards", this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
//		try {
		Player player = (Player) sender;

		BPlayerBoard board = Netherboard.instance().createBoard(player, "Bear Nation :O");

		board.set("&bHello", 1);
		board.set(colorize("&bHello 2"), 2);
		for (int i = 0; i < 200; i = i + 5) {
			final int finalI = i;
			BNCore.runTaskLater(i, () -> board.setName("Bear Nation :O " + finalI));
		}

		return true;
//		} catch (InvalidInputException ex) {
//			sender.sendMessage(PREFIX + ex.getMessage());
//			return true;
//		}
	}

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
		return null;
	}

}
