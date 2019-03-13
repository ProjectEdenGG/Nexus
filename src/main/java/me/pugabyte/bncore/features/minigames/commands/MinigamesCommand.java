package me.pugabyte.bncore.features.minigames.commands;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.exceptions.MinigameException;
import me.pugabyte.bncore.features.minigames.models.exceptions.NotInAMatchException;
import me.pugabyte.bncore.models.exceptions.InvalidInputException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static me.pugabyte.bncore.features.minigames.Minigames.PREFIX;

public class MinigamesCommand implements CommandExecutor, TabCompleter {
	public MinigamesCommand() {
		BNCore.registerCommand("newminigames", this);
		BNCore.registerTabCompleter("newminigames", this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			if (!(sender instanceof Player)) {
				throw new InvalidInputException("You must be in-game to run this command");
			}

			Player player = (Player) sender;
			Minigamer minigamer = PlayerManager.get(player);
			if (args.length == 0) {
				minigamer.tell("Help menu");
			} else {
				switch (args[0].toLowerCase()) {
					case "join":
						if (args.length > 1) {
							minigamer.join(args[1]);
						} else {
							minigamer.tell("You must supply an arena name to join");
						}
						break;
					case "quit":
						if (minigamer.getMatch() != null) {
							minigamer.quit();
						} else {
							throw new NotInAMatchException();
						}
						break;
					case "scores":
						if (minigamer.getMatch() != null) {
							minigamer.tell("Your score: " + minigamer.getScore());
							minigamer.tell("Your team's score: " + minigamer.getTeam().getScore(minigamer.getMatch()));
						} else {
							throw new NotInAMatchException();
						}
						break;
					case "reload": {
						long startTime = System.currentTimeMillis();
						if (args.length > 1) {
							Arena.read(args[1]);
						} else {
							Arena.read();
						}
						long stopTime = System.currentTimeMillis();
						long elapsedTime = stopTime - startTime;
						BNCore.log("Reload time took " + elapsedTime + "ms");
						break;
					}
					case "save": {
						long startTime = System.currentTimeMillis();
						if (args.length > 1) {
							Arena.write(args[1]);
						} else {
							Arena.write();
						}
						long stopTime = System.currentTimeMillis();
						long elapsedTime = stopTime - startTime;
						BNCore.log("Save time took " + elapsedTime + "ms");
						break;
					}
					case "dump":
						if (args.length > 1) {
							Optional<Arena> optionalArena = ArenaManager.get(args[1]);
							if (optionalArena.isPresent()) {
								BNCore.dump(optionalArena.get());
							} else {
								minigamer.tell("Arena not found");
							}
						} else {
							minigamer.tell("You must supply an arena name");
						}
						break;
				}
			}
		} catch (InvalidInputException | MinigameException ex) {
			sender.sendMessage(PREFIX + ex.getMessage());
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> completions = new ArrayList<>();
		if (args.length == 1) {
			completions.add("join");
			completions.add("quit");
			completions.add("scores");
			completions.add("reload");
			completions.add("save");
			completions.add("dump");
		} else if (args.length == 2 && (args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("save"))) {
			ArenaManager.getAll().forEach(arena -> completions.add(arena.getName()));
		}

		return completions;
	}

}
