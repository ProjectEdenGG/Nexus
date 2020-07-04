package me.pugabyte.bncore.features.documentation.commands;

import me.pugabyte.bncore.features.documentation.commands.models.Command;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.server.TabCompleteEvent;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class DocumentCommands {
	private static SimpleCommandMap commandMap = invokeMethod(Bukkit.getServer().getClass(), "getCommandMap", Bukkit.getServer());
	private List<Command> commands;
//	private CommandsDatabase.CommandsReader commandsReader = new CommandsDatabase.CommandsReader();
//	private CommandsDatabase.CommandsWriter commandsWriter = new CommandsDatabase.CommandsWriter();

	public DocumentCommands() {
//		commands = (ArrayList<Command>) commandsReader.read();

		findNewCommands();
	}

	@SuppressWarnings("unchecked")
	public static <T> T invokeMethod(Class<?> clz, String method, Object instance, Object... parameters) {
		try {
			Class<?>[] parameterTypes = new Class<?>[parameters.length];
			int x = 0;
			for (Object obj : parameters)
				if (obj != null)
					parameterTypes[x++] = obj.getClass();
				else
					parameterTypes[x++] = null;

			Method m = clz.getDeclaredMethod(method, parameterTypes);
			m.setAccessible(true);
			return (T) m.invoke(instance, parameters);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private void findNewCommands() {
		Tasks.waitAsync(20, () -> {
			List<String> offers = commandMap.tabComplete(Bukkit.getConsoleSender(), "");
			TabCompleteEvent tabEvent = new TabCompleteEvent(Bukkit.getConsoleSender(), "", offers);
			tabEvent.callEvent();

			List<String> eventCompletions = tabEvent.getCompletions();
			List<String> completions = eventCompletions.stream()
					.filter(completion -> completion.contains(":"))
					.collect(Collectors.toList());

			for (String commandString : completions) {
				Command command;
				String name;
				String plugin;

				String[] split = commandString.split(":");
				if (split.length == 2 && split[0].length() > 0) {
					name = split[1];
					plugin = split[0];

					PluginCommand pluginCommand = Bukkit.getServer().getPluginCommand(name);
					if (pluginCommand != null) {
						name = pluginCommand.getName();
						plugin = pluginCommand.getPlugin().getName();
					}

					command = new Command(name, plugin);

					if (isDuplicate(command)) continue;

					command.setEnabled(true);

					if (pluginCommand != null) {
						command.setUsage(pluginCommand.getUsage());
						if (command.getUsage().startsWith("/ ")) {
							command.setUsage(command.getUsage().replaceAll("/ ", command.getCommand()));
						}
						if (command.getUsage().contains("<command>")) {
							command.setUsage(command.getUsage().replaceAll("<command>", command.getCommand()));
						}
						command.setDescription(pluginCommand.getDescription());
						command.setAliases(new HashSet<>(pluginCommand.getAliases()));
					}

					if (command.getUsage() != null && command.getUsage().length() > 150)
						command.setUsage(null);

					if (command.getDescription() != null && command.getDescription().length() > 150)
						command.setDescription(null);

					if (command.getAliases() != null && String.join(",", command.getAliases()).length() > 150)
						command.setAliases(null);

					commands.add(command);
				}
			}

			for (Command _command : commands) {
//				commandsWriter.write(_command);
			}
		});
	}

	private boolean isDuplicate(Command command) {
		for (Command _command : commands) {
			if (command.getCommand().equals(_command.getCommand())) {
				if (command.getPlugin().equals(_command.getPlugin())) {
					return true;
				}
			}
		}
		return false;
	}
}
