package me.pugabyte.bncore.features.minigames.commands;

import me.pugabyte.bncore.BNCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MinigamesCommandHandler implements CommandExecutor, TabCompleter {

	public MinigamesCommandHandler() {
		BNCore.registerCommand("newminigames", this);
		BNCore.registerTabCompleter("newminigames", this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String name;
		String[] subArgs = new String[0];
		if (args.length == 0) {
			name = "help";
		} else {
			name = args[0];
			subArgs = Arrays.copyOfRange(args, 1, args.length);
		}

		MinigamesCommandEvent event = new MinigamesCommandEvent(sender, subArgs);

		MinigamesCommands.get(name).run(event);

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> completions = new ArrayList<>();
		if (args.length == 1) {
			completions.addAll(MinigamesCommands.getNames(args[0]));
		} else {
			MinigamesCommand command = MinigamesCommands.get(args[0]);
			MinigamesTabEvent event = new MinigamesTabEvent(sender, Arrays.copyOfRange(args, 1, args.length));

			List<String> results = command.run(event);
			if (results != null)
				completions.addAll(results);
		}

		return completions;
	}

}
