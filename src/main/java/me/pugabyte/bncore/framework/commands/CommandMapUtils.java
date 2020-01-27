package me.pugabyte.bncore.framework.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.DoubleSlash;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings("unchecked")
class CommandMapUtils {
	private final Plugin plugin;
	private final Constructor<PluginCommand> COMMAND_CONSTRUCTOR;
	private final Field COMMAND_MAP_FIELD;
	private final Field KNOWN_COMMANDS_FIELD;

	CommandMapUtils(Plugin plugin) {
		this.plugin = plugin;
		try {
			COMMAND_CONSTRUCTOR = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
			COMMAND_CONSTRUCTOR.setAccessible(true);

			COMMAND_MAP_FIELD = SimplePluginManager.class.getDeclaredField("commandMap");
			COMMAND_MAP_FIELD.setAccessible(true);

			KNOWN_COMMANDS_FIELD = SimpleCommandMap.class.getDeclaredField("knownCommands");
			KNOWN_COMMANDS_FIELD.setAccessible(true);
		} catch (NoSuchMethodException | NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	private CommandMap getCommandMap() {
		try {
			return (CommandMap) COMMAND_MAP_FIELD.get(Bukkit.getServer().getPluginManager());
		} catch (Exception e) {
			throw new RuntimeException("Could not get CommandMap", e);
		}
	}

	private Map<String, Command> getKnownCommandMap() {
		try {
			return (Map<String, Command>) KNOWN_COMMANDS_FIELD.get(getCommandMap());
		} catch (Exception e) {
			throw new RuntimeException("Could not get known commands map", e);
		}
	}

	void register(String name, CustomCommand customCommand) throws IllegalAccessException, InvocationTargetException, InstantiationException {
		if (customCommand.getClass().getAnnotation(DoubleSlash.class) != null)
			name = "/" + name;
		CommandHandler handler = new CommandHandler(customCommand);

		PluginCommand pluginCommand = COMMAND_CONSTRUCTOR.newInstance(name, plugin);
		pluginCommand.setLabel(name);
		pluginCommand.setAliases(customCommand.getAliases());
		pluginCommand.setExecutor(handler);
		pluginCommand.setTabCompleter(handler);

		getCommandMap().register(plugin.getDescription().getName(), pluginCommand);
		getKnownCommandMap().put(plugin.getDescription().getName().toLowerCase() + ":" + name, pluginCommand);
		getKnownCommandMap().put(name, pluginCommand);
	}

	void unregister(String name) throws IllegalAccessException {
		CommandMap map = getCommandMap();
		Map<String, Command> knownCommands = (Map<String, Command>) KNOWN_COMMANDS_FIELD.get(map);
		Iterator<Command> iterator = knownCommands.values().iterator();

		while (iterator.hasNext()) {
			Command command = iterator.next();
			if (command instanceof PluginCommand && name.equals(command.getLabel())) {
				command.unregister(map);
				iterator.remove();
			}
		}
	}

}
