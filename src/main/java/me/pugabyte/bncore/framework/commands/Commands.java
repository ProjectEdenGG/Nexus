package me.pugabyte.bncore.framework.commands;

import lombok.Getter;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleterFor;
import org.bukkit.plugin.Plugin;
import org.objenesis.ObjenesisStd;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
	private static Map<Class<?>, Method> tabCompleters = new HashMap<>();

	public Commands(Plugin plugin, String path) {
		this.plugin = plugin;
		this.path = path;
		this.mapUtils = new CommandMapUtils(plugin);
		this.commandSet = new Reflections(path).getSubTypesOf(CustomCommand.class);
		registerTabCompleters();
	}

	public static CustomCommand get(String alias) {
		if (commands.containsKey(alias))
			return commands.get(alias);
		return null;
	}

	public void registerAll() {
		for (Class<? extends CustomCommand> command : commandSet) {
			register(new ObjenesisStd().newInstance(command));
		}
	}

	private void register(CustomCommand customCommand) {
		for (String alias : customCommand.getAliases()) {
			try {
				// plugin.getLogger().info("Registering command " + alias);
				mapUtils.register(alias, customCommand);
				commands.put(alias, customCommand);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void unregisterAll() {
		for (Class<? extends CustomCommand> command : commandSet) {
			unregister(new ObjenesisStd().newInstance(command));
		}
	}

	private void unregister(CustomCommand customCommand) {
		for (String alias : customCommand.getAliases()) {
			try {
				// plugin.getLogger().info("Unregistering command " + alias);
				mapUtils.unregister(alias);
				commands.remove(alias);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void registerTabCompleters() {
		new Reflections(path).getSubTypesOf(CustomCommand.class).forEach(this::registerTabCompleters);
		registerTabCompleters(CustomCommand.class);
	}

	private void registerTabCompleters(Class<?> clazz) {
		getTabCompleterMethods(clazz).forEach(method -> {
			Class<?>[] classes = method.getAnnotation(TabCompleterFor.class).value();
			for (Class<?> classFor : classes) {
				method.setAccessible(true);
				tabCompleters.put(classFor, method);
			}
		});
	}

	Set<Method> getTabCompleterMethods(Class<?> clazz) {
		Set<Method> methods = getMethods(clazz, withAnnotation(TabCompleterFor.class));
		if (methods.size() == 1)
			return Collections.singleton(methods.iterator().next());
		return methods;
	}


}
