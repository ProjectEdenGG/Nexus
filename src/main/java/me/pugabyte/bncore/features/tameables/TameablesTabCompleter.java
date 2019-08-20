package me.pugabyte.bncore.features.tameables;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.tameables.models.TameablesAction;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
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

public class TameablesTabCompleter implements TabCompleter {

	TameablesTabCompleter() {
		// BNCore.registerTabCompleter("tameables", this);
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
