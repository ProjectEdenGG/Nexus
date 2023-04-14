package gg.projecteden.nexus.framework.commandsv2;

import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMeta;
import gg.projecteden.nexus.utils.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;

@SuppressWarnings("unchecked")
public class CommandMapUtils {
	private final Plugin plugin;
	private final Constructor<PluginCommand> COMMAND_CONSTRUCTOR;
	private final Field COMMAND_MAP_FIELD;
	private final Field KNOWN_COMMANDS_FIELD;
	@Getter
	private final SimpleCommandMap commandMap;
	@Getter
	private final Map<String, Command> knownCommandMap;

	CommandMapUtils(Plugin plugin) {
		this.plugin = plugin;
		try {
			COMMAND_CONSTRUCTOR = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
			COMMAND_CONSTRUCTOR.setAccessible(true);

			COMMAND_MAP_FIELD = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
			COMMAND_MAP_FIELD.setAccessible(true);

			KNOWN_COMMANDS_FIELD = SimpleCommandMap.class.getDeclaredField("knownCommands");
			KNOWN_COMMANDS_FIELD.setAccessible(true);

			commandMap = (SimpleCommandMap) COMMAND_MAP_FIELD.get(Bukkit.getServer().getPluginManager());
			knownCommandMap = (Map<String, Command>) KNOWN_COMMANDS_FIELD.get(commandMap);
		} catch (NoSuchMethodException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public void register(String name, CustomCommandMeta commandMeta) throws IllegalAccessException, InvocationTargetException, InstantiationException {
		name = name.toLowerCase();
		CommandHandler handler = new CommandHandler(commandMeta.getInstance());

		PluginCommand pluginCommand = COMMAND_CONSTRUCTOR.newInstance(name, plugin);
		pluginCommand.setLabel(name);
		pluginCommand.setAliases(commandMeta.getAliases());
		pluginCommand.setExecutor(handler);

		if (!isNullOrEmpty(commandMeta.getDescription()))
			pluginCommand.setDescription(commandMeta.getDescription());
		if (!isNullOrEmpty(commandMeta.getPermission()))
			pluginCommand.setPermission(commandMeta.getPermission());

		commandMap.register(plugin.getDescription().getName(), pluginCommand);
		knownCommandMap.put(plugin.getDescription().getName().toLowerCase() + ":" + name, pluginCommand);
		knownCommandMap.put(name, pluginCommand);


	}

	public void unregister(String name) {
		Utils.removeIf(
			command -> command instanceof PluginCommand && name.equals(command.getLabel()),
			command -> command.unregister(commandMap),
			knownCommandMap.values()
		);
	}

}
