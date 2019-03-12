package me.pugabyte.bncore.features.minigames.commands;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.models.exceptions.InvalidInputException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.pugabyte.bncore.features.minigames.Minigames.PREFIX;
import static me.pugabyte.bncore.features.minigames.Minigames.getPlayerManager;

public class MinigamesCommand implements CommandExecutor {
	public MinigamesCommand() {
		BNCore.registerCommand("newminigames", this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			if (!(sender instanceof Player)) {
				throw new InvalidInputException("You must be in-game to run this command");
			}

			Player player = (Player) sender;
			Minigamer minigamer = getPlayerManager().get(player);
			if (args.length == 0) {
				player.sendMessage(PREFIX + "Help menu");
			} else {
				switch (args[0].toLowerCase()) {
					case "join":
						if (args.length > 1) {
							minigamer.join(args[1]);
						} else {
							player.sendMessage(PREFIX + "You must supply an arena name to join");
						}
						break;
					case "quit":
						if (minigamer.getMatch() != null) {
							minigamer.quit();
						}
				}
			}
		} catch (InvalidInputException ex) {
			sender.sendMessage(PREFIX + ex.getMessage());
		}
		return true;
	}
}
