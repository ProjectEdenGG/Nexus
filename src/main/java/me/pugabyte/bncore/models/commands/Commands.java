package me.pugabyte.bncore.models.commands;

import me.pugabyte.bncore.models.commands.models.CustomCommand;
import org.bukkit.plugin.Plugin;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public class Commands {
	private Plugin plugin;
	private CommandMapUtils mapUtils;
	private Set<Class<? extends CustomCommand>> commandSet;
	private static Map<String, CustomCommand> commands = new HashMap<>();

	public Commands(Plugin plugin, String path) {
		this.plugin = plugin;
		this.mapUtils = new CommandMapUtils(plugin);
		this.commandSet = new Reflections(path).getSubTypesOf(CustomCommand.class);
	}

	public static CustomCommand get(String alias) {
		if (commands.containsKey(alias))
			return commands.get(alias);
		return null;
	}

	public void registerAll() {
		for (Class<? extends CustomCommand> command : commandSet) {
			try {
				register(command.newInstance());
			} catch (InstantiationException | IllegalAccessException ex) {
				plugin.getLogger().info("Error registering command " + command.getName());
				ex.printStackTrace();
			}
		}
	}

	private void register(CustomCommand customCommand) {
		for (String alias : customCommand.getAliases()) {
			try {
				plugin.getLogger().info("Registering command " + alias);
				mapUtils.register(alias, customCommand);
				commands.put(alias, customCommand);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void unregisterAll() {
		for (Class<? extends CustomCommand> command : commandSet) {
			try {
				unregister(command.newInstance());
			} catch (InstantiationException | IllegalAccessException ex) {
				plugin.getLogger().info("Error registering command " + command.getName());
				ex.printStackTrace();
			}
		}
	}

	private void unregister(CustomCommand customCommand) {
		for (String alias : customCommand.getAliases()) {
			try {
				plugin.getLogger().info("Unregistering command " + alias);
				mapUtils.unregister(alias);
				commands.remove(alias);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
