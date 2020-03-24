package me.pugabyte.bncore.framework.commands;

import lombok.Getter;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.bncore.utils.Time.Timer;
import org.bukkit.plugin.Plugin;
import org.objenesis.ObjenesisStd;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static me.pugabyte.bncore.utils.StringUtils.listLast;
import static org.reflections.ReflectionUtils.getMethods;
import static org.reflections.ReflectionUtils.withAnnotation;

@SuppressWarnings("unused")
public class Commands {
	private Plugin plugin;
	private final String path;
	private CommandMapUtils mapUtils;
	private Set<Class<? extends CustomCommand>> commandSet;
	private static Map<String, CustomCommand> commands = new HashMap<>();
	@Getter
	private static Map<Class<?>, Method> converters = new HashMap<>();
	@Getter
	private static Map<Class<?>, Method> tabCompleters = new HashMap<>();
	@Getter
	private static Map<String, String> redirects = new HashMap<>();

	public Commands(Plugin plugin, String path) {
		this.plugin = plugin;
		this.path = path;
		this.mapUtils = new CommandMapUtils(plugin);
		this.commandSet = new Reflections(path).getSubTypesOf(CustomCommand.class);
		registerConvertersAndTabCompleters();
		plugin.getServer().getPluginManager().registerEvents(new CommandListener(), plugin);
	}

	public static CustomCommand get(String alias) {
		if (commands.containsKey(alias))
			return commands.get(alias);
		return null;
	}

	public void registerAll() {
		for (Class<? extends CustomCommand> command : commandSet)
			register(new ObjenesisStd().newInstance(command));
		plugin.getLogger().info("Registered " + commandSet.size() + " commands");
	}

	private void register(CustomCommand customCommand) {
		new Timer("  Register command " + customCommand.getName(), () -> {
			if (listLast(customCommand.getClass().toString(), ".").startsWith("_")) return;

			try {
				for (String alias : customCommand.getAllAliases()) {
					mapUtils.register(alias, customCommand);
					commands.put(alias, customCommand);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public void unregisterAll() {
		for (Class<? extends CustomCommand> command : commandSet)
			unregister(new ObjenesisStd().newInstance(command));
	}

	private void unregister(CustomCommand customCommand) {
		try {
			for (String alias : customCommand.getAllAliases()) {
				mapUtils.unregister(customCommand.getName());
				commands.remove(alias);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void registerConvertersAndTabCompleters() {
		commandSet.forEach(this::registerTabCompleters);
		commandSet.forEach(this::registerConverters);
		registerTabCompleters(CustomCommand.class);
		registerConverters(CustomCommand.class);
	}

	private void registerTabCompleters(Class<?> clazz) {
		getMethods(clazz, withAnnotation(TabCompleterFor.class)).forEach(method -> {
			for (Class<?> classFor : method.getAnnotation(TabCompleterFor.class).value()) {
				method.setAccessible(true);
				tabCompleters.put(classFor, method);
			}
		});
	}

	private void registerConverters(Class<?> clazz) {
		getMethods(clazz, withAnnotation(ConverterFor.class)).forEach(method -> {
			for (Class<?> classFor : method.getAnnotation(ConverterFor.class).value()) {
				method.setAccessible(true);
				converters.put(classFor, method);
			}
		});
	}

}
