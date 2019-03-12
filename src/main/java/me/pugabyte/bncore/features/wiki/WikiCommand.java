package me.pugabyte.bncore.features.wiki;

import me.pugabyte.bncore.BNCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class WikiCommand implements CommandExecutor {
	private final String URL = "https://wiki.bnn.gg";

	WikiCommand() {
		BNCore.registerCommand("wiki", this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0 || !(args[0].equalsIgnoreCase("search"))) {
			sender.sendMessage("§eVisit our wiki at §3" + URL);
			sender.sendMessage("§eOr use §c/wiki search <query> §eto search the wiki from ingame.");
		} else {
			Wiki.search(sender, args, "Wiki");
		}

		return true;
	}
}
