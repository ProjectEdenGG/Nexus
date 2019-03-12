package me.pugabyte.bncore.features.wiki;

import me.pugabyte.bncore.BNCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MCWikiCommand implements CommandExecutor {
	private final String URL = "https://minecraft.gamepedia.com/";

	MCWikiCommand() {
		BNCore.registerCommand("mcwiki", this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0 || !(args[0].equalsIgnoreCase("search"))) {
			sender.sendMessage("§eVisit the minecraft wiki at §3" + URL);
			sender.sendMessage("§eOr use §c/mcwiki search <query> §eto search the wiki from ingame.");
		} else {
			Wiki.search(sender, args, "MCWiki");
		}

		return true;
	}
}
