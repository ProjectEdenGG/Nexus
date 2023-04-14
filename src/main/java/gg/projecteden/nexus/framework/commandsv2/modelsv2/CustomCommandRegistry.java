package gg.projecteden.nexus.framework.commandsv2.modelsv2;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Redirects.Redirect;
import gg.projecteden.nexus.framework.commandsv2.Commands;
import gg.projecteden.nexus.framework.commandsv2.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commandsv2.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.utils.Timer;
import gg.projecteden.nexus.utils.Utils;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static gg.projecteden.api.common.utils.ReflectionUtils.methodsAnnotatedWith;
import static gg.projecteden.api.common.utils.ReflectionUtils.subTypesOf;
import static gg.projecteden.nexus.framework.commandsv2.Commands.prettyName;

@Data
@SuppressWarnings({"unused", "unchecked"})
public class CustomCommandRegistry {
	private final Commands commands;
	private final Set<Class<? extends CustomCommand>> commandClasses;
	private final Map<String, CustomCommandMeta> registeredCommandsByAlias = new HashMap<>();
	private final Map<Class<?>, Method> converters = new HashMap<>();
	private final Map<Class<?>, Method> tabCompleters = new HashMap<>();
	private final Map<String, String> redirects = new HashMap<>();

	public CustomCommandRegistry(Commands commands) {
		this.commands = commands;
		this.commandClasses = (Set<Class<? extends CustomCommand>>) subTypesOf(CustomCommand.class, commands.getPaths().toArray(String[]::new)).stream()
			.map(clazz -> (Class<? extends CustomCommand>) clazz)
			.toList();
		registerConvertersAndTabCompleters();
	}

	public void registerAll() {
		Nexus.debug(" Registering " + commandClasses.size() + " commands");
		commandClasses.forEach(this::register);
	}

	public void register(Class<? extends CustomCommand>... customCommands) {
		for (Class<? extends CustomCommand> clazz : customCommands)
			try {
				if (Utils.canEnable(clazz))
					register(new CustomCommandMetaReader(clazz).read());
			} catch (Throwable ex) {
				commands.getPlugin().getLogger().info("Error while registering command " + prettyName(clazz));
				ex.printStackTrace();
			}
	}

	public void registerExcept(Class<? extends CustomCommand>... customCommands) {
		List<Class<? extends CustomCommand>> excluded = Arrays.asList(customCommands);
		for (Class<? extends CustomCommand> clazz : commandClasses)
			if (!excluded.contains(clazz))
				register(clazz);
	}

	private void register(CustomCommandMeta commandMeta) {
		new Timer("  Register command " + commandMeta.getName(), () -> {
			try {
				for (String alias : commandMeta.getAllAliases()) {
					commands.getMapUtils().register(alias, commandMeta);
					registeredCommandsByAlias.put(alias.toLowerCase(), commandMeta);
					registerRedirects(commandMeta);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			Utils.tryRegisterListener(commandMeta.getInstance());
		});
	}

	public void unregisterAll() {
		for (Class<? extends CustomCommand> clazz : commandClasses)
			try {
				unregister(clazz);
			} catch (Throwable ex) {
				commands.getPlugin().getLogger().info("Error while unregistering command " + prettyName(clazz));
				ex.printStackTrace();
			}
	}

	public void unregister(Class<? extends CustomCommand>... customCommands) {
		for (Class<? extends CustomCommand> clazz : customCommands)
			if (Utils.canEnable(clazz))
				unregister(Nexus.singletonOf(clazz));
	}

	public void unregisterExcept(Class<? extends CustomCommand>... customCommands) {
		List<Class<? extends CustomCommand>> excluded = Arrays.asList(customCommands);
		for (Class<? extends CustomCommand> clazz : commandClasses)
			if (!excluded.contains(clazz))
				unregister(clazz);
	}

	private void unregister(CustomCommand customCommand) {
		new Timer("  Unregister command " + customCommand.getName(), () -> {
			try {
				commands.getMapUtils().unregister(customCommand.getName());
				for (String alias : customCommand.getAllAliases())
					registeredCommandsByAlias.remove(alias);
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

	private void registerRedirects(CustomCommandMeta commandMeta) {
		for (Redirect annotation : commandMeta.getRedirects())
			for (String from : annotation.from())
				redirects.put(from, annotation.to());
	}

	private void registerConvertersAndTabCompleters() {
		commandClasses.forEach(this::registerTabCompleters);
		commandClasses.forEach(this::registerConverters);
		registerTabCompleters(CustomCommand.class);
		registerConverters(CustomCommand.class);
	}

	private void registerTabCompleters(Class<?> clazz) {
		methodsAnnotatedWith(clazz, TabCompleterFor.class).forEach(method -> {
			for (Class<?> classFor : method.getAnnotation(TabCompleterFor.class).value()) {
				method.setAccessible(true);
				tabCompleters.put(classFor, method);
			}
		});
	}

	private void registerConverters(Class<?> clazz) {
		methodsAnnotatedWith(clazz, ConverterFor.class).forEach(method -> {
			for (Class<?> classFor : method.getAnnotation(ConverterFor.class).value()) {
				method.setAccessible(true);
				converters.put(classFor, method);
			}
		});
	}


}
