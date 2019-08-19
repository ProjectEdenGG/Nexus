package me.pugabyte.bncore.features.tameables;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.tameables.models.TameablesAction;
import me.pugabyte.bncore.models.exceptions.postconfigured.InvalidInputException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static me.pugabyte.bncore.features.tameables.Tameables.PREFIX;

public class TameablesCommand implements CommandExecutor, TabCompleter {
	private final static String USAGE = "Correct usage: " + ChatColor.RED + "/tameables <transfer [player]|untame>";

	TameablesCommand() {
		BNCore.registerCommand("tameables", this);
		BNCore.registerTabCompleter("tameables", this);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			if (!(sender instanceof Player)) {
				throw new InvalidInputException("You must be in-game to run this command");
			}

			Player player = (Player) sender;

			if (args.length == 0) {
				throw new InvalidInputException(USAGE);
			} else {
				switch (args[0].toLowerCase()) {
					case "transfer":
						if (args.length != 2) throw new InvalidInputException(USAGE);
						Optional<? extends Player> transfer = Bukkit.getOnlinePlayers().stream()
								.filter(p -> p.getName().toLowerCase().startsWith(args[1].toLowerCase()))
								.findFirst();

						if (transfer.isPresent()) {
							TameablesAction action = TameablesAction.TRANSFER;
							action.setPlayer(transfer.get());
							BNCore.tameables.addPendingAction(player, action);
							player.sendMessage(PREFIX + "Punch the animal you wish to transfer to " + action.getPlayer().getName());
						} else {
							throw new InvalidInputException(ChatColor.RED + "Could not find that player! Are they online?");
						}
						break;
					case "untame":
						BNCore.tameables.addPendingAction(player, TameablesAction.UNTAME);
						player.sendMessage(PREFIX + "Punch the animal you wish to remove ownership of");
						break;
					case "view":
					case "info":
						BNCore.tameables.addPendingAction(player, TameablesAction.INFO);
						player.sendMessage(PREFIX + "Punch the animal you wish to view information on");
						break;
					default:
						throw new InvalidInputException(USAGE);
				}
			}

			return true;
		} catch (InvalidInputException ex) {
			sender.sendMessage(PREFIX + ex.getMessage());
			return true;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> completions = new ArrayList<>();
		if (args.length == 1) {
			for (TameablesAction action : TameablesAction.values()) {
				completions.add(action.name().toLowerCase());
			}
		} else {
			for (Player player : Bukkit.getOnlinePlayers()) {
				completions.add(player.getName());
			}
		}
		return completions;
	}
}
