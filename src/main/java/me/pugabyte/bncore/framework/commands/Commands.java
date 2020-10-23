package me.pugabyte.bncore.framework.commands;

import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.ICustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.bncore.framework.commands.models.annotations.DoubleSlash;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.bncore.utils.Time.Timer;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.plugin.Plugin;
import org.objenesis.ObjenesisStd;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.reflections.ReflectionUtils.getMethods;
import static org.reflections.ReflectionUtils.withAnnotation;

@SuppressWarnings({"unused", "unchecked"})
public class Commands {
	private final Plugin plugin;
	private final String path;
	private final CommandMapUtils mapUtils;
	private final Set<Class<? extends CustomCommand>> commandSet;
	@Getter
	private static final Map<String, CustomCommand> commands = new HashMap<>();
	@Getter
	private static final Map<Class<?>, Method> converters = new HashMap<>();
	@Getter
	private static final Map<Class<?>, Method> tabCompleters = new HashMap<>();
	@Getter
	private static final Map<String, String> redirects = new HashMap<>();
	@Getter
	private static final String pattern = "\\/(\\/|)[a-zA-Z0-9\\-_]+";

	public Commands(Plugin plugin, String path) {
		this.plugin = plugin;
		this.path = path;
		this.mapUtils = new CommandMapUtils(plugin);
		this.commandSet = new Reflections(path).getSubTypesOf(CustomCommand.class);
		registerConvertersAndTabCompleters();
		plugin.getServer().getPluginManager().registerEvents(new CommandListener(), plugin);
	}

	public static CustomCommand get(String alias) {
		return commands.getOrDefault(alias, null);
	}

	public static CustomCommand get(Class<? extends CustomCommand> clazz) {
		return commands.getOrDefault(prettyName(clazz), null);
	}

	public static String prettyName(ICustomCommand customCommand) {
		return prettyName(customCommand.getClass());
	}

	public static String prettyName(Class<? extends ICustomCommand> clazz) {
		return clazz.getSimpleName().replaceAll("Command$", "");
	}

	public void registerAll() {
		commandSet.forEach(this::register);
	}

	public void register(Class<? extends CustomCommand>... customCommands) {
		for (Class<? extends CustomCommand> clazz : customCommands)
			try {
				if (Utils.canEnable(clazz))
					register(new ObjenesisStd().newInstance(clazz));
			} catch (Throwable ex) {
				BNCore.log("Error while registering command " + prettyName(clazz));
				ex.printStackTrace();
			}
	}

	public void registerExcept(Class<? extends CustomCommand>... customCommands) {
		List<Class<? extends CustomCommand>> excluded = Arrays.asList(customCommands);
		for (Class<? extends CustomCommand> clazz : commandSet)
			if (!excluded.contains(clazz))
				register(clazz);
	}

	private void register(CustomCommand customCommand) {
		new Timer("  Register command " + customCommand.getName(), () -> {
			try {
				for (String alias : customCommand.getAllAliases()) {
					mapUtils.register(alias, customCommand);

					if (customCommand.getClass().getAnnotation(DoubleSlash.class) != null) alias = "/" + alias;
					commands.put(alias.toLowerCase(), customCommand);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			Utils.tryRegisterListener(customCommand);
		});
	}

	public void unregisterAll() {
		for (Class<? extends CustomCommand> clazz : commandSet)
			try {
				if (!Modifier.isAbstract(clazz.getModifiers()))
					unregister(clazz);
			} catch (Throwable ex) {
				BNCore.log("Error while unregistering command " + prettyName(clazz));
				ex.printStackTrace();
			}
	}

	public void unregister(Class<? extends CustomCommand>... customCommands) {
		for (Class<? extends CustomCommand> clazz : customCommands)
			unregister(new ObjenesisStd().newInstance(clazz));
	}

	public void unregisterExcept(Class<? extends CustomCommand>... customCommands) {
		List<Class<? extends CustomCommand>> excluded = Arrays.asList(customCommands);
		for (Class<? extends CustomCommand> clazz : commandSet)
			if (!excluded.contains(clazz))
				unregister(clazz);
	}

	private void unregister(CustomCommand customCommand) {
		new Timer("  Unregister command " + customCommand.getName(), () -> {
			try {
				mapUtils.unregister(customCommand.getName());
				for (String alias : customCommand.getAllAliases())
					commands.remove(alias);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				customCommand._shutdown();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
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
