package me.pugabyte.bncore.features.minigames.commands;

import me.pugabyte.bncore.features.minigames.commands.commands.DumpCommand;
import me.pugabyte.bncore.features.minigames.commands.commands.HelpCommand;
import me.pugabyte.bncore.features.minigames.commands.commands.JoinCommand;
import me.pugabyte.bncore.features.minigames.commands.commands.QuitCommand;
import me.pugabyte.bncore.features.minigames.commands.commands.ReloadCommand;
import me.pugabyte.bncore.features.minigames.commands.commands.SaveCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MinigamesCommands {
	private static Map<String, MinigamesCommand> commands = new HashMap<>();

	public MinigamesCommands() {
		new MinigamesCommandHandler();

		add(new DumpCommand());
		add(new HelpCommand());
		add(new JoinCommand());
		add(new QuitCommand());
		add(new ReloadCommand());
		add(new SaveCommand());
	}

	public static MinigamesCommand get(String command) {
		return commands.getOrDefault(command, commands.get("help"));
	}

	public static List<String> getNames() {
		List<String> names = new ArrayList<>();
		for (Map.Entry<String, MinigamesCommand> commandEntry : commands.entrySet()) {
			names.add(commandEntry.getValue().getName());
		}
		return names;
	}

	public static List<String> getNames(String filter) {
		List<String> names = new ArrayList<>();
		for (Map.Entry<String, MinigamesCommand> commandEntry : commands.entrySet())
			if (commandEntry.getValue().getName().startsWith(filter))
				names.add(commandEntry.getValue().getName());
		return names;
	}

	public static void add(MinigamesCommand command) {
		commands.put(command.getName(), command);
	}

}
