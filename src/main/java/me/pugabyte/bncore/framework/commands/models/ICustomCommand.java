package me.pugabyte.bncore.framework.commands.models;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.commands.models.events.TabEvent;
import me.pugabyte.bncore.framework.exceptions.BNException;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.framework.exceptions.preconfigured.NoPermissionException;
import org.bukkit.command.CommandSender;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static me.pugabyte.bncore.utils.Utils.listLast;
import static org.reflections.ReflectionUtils.getMethods;
import static org.reflections.ReflectionUtils.withAnnotation;

@SuppressWarnings("unused")
public interface ICustomCommand {

	default void execute(CommandEvent event) {
		try {
			CustomCommand command = getCommand(event);
			event.setCommand(command);
			Method method = getMethod(command, event.getArgs());
			if (!hasPermission(event.getSender(), method))
				throw new NoPermissionException();
			command.invoke(method, event);
		} catch (Exception ex) {
			event.handleException(ex);
		}
	}

	default List<String> tabComplete(TabEvent event) {
		return new PathParser(getPathMethods(event.getCommand())).tabComplete(event);
	}

	default String getName() {
		return getAliases().get(0);
	}

	default List<String> getAliases() {
		String name = listLast(this.getClass().toString(), ".").replaceAll("Command", "");
		List<String> aliases = new ArrayList<>(Collections.singletonList(name.toLowerCase()));

		for (Annotation annotation : this.getClass().getAnnotations()) {
			if (annotation instanceof Aliases) {
				for (String alias : ((Aliases) annotation).value()) {
					if (!Pattern.compile("[a-zA-Z0-9_-]+").matcher(alias).matches()) {
						BNCore.warn("Alias invalid: " + name + "Command.java / " + alias);
						continue;
					}

					aliases.add(alias);
				}
			}
		}

		return aliases;
	}

	default String getPermission() {
		for (Annotation annotation : this.getClass().getAnnotations()) {
			if (annotation instanceof Permission) {
				return ((Permission) annotation).value();
			}
		}

		return null;
	}

	default void invoke(Method method, CommandEvent event) throws Exception {
		List<String> args = event.getArgs();
		List<Parameter> parameters = Arrays.asList(method.getParameters());
		Iterator<String> path = Arrays.asList(method.getAnnotation(Path.class).value().split(" ")).iterator();
		Object[] objects = new Object[parameters.size()];

		int i = 1;
		int pathIndex = 0;
		for (Parameter parameter : parameters) {
			Arg annotation = parameter.getDeclaredAnnotation(Arg.class);
			if (annotation == null)
				throw new BNException("Command parameter not annotated with @Arg: "
						+ method.getName() + "(" + parameter.getType().getName() + " " + parameter.getName() + ")");

			String pathArg = "";
			while (!pathArg.startsWith("{") && !pathArg.startsWith("[") && !pathArg.startsWith("<")) {
				pathArg = path.next();
				++pathIndex;
			}

			String value = annotation.value();
			if (args.size() >= pathIndex) {
				if (pathArg.contains("..."))
					value = String.join(" ", args.subList(pathIndex - 1, args.size()));
				else
					value = args.get(pathIndex - 1);
			}

			objects[i - 1] = convert(value, parameter.getType());
			++i;
		}

		method.setAccessible(true);
		method.invoke(this, objects);
	}

	Object convert(String value, Class<?> type);

	@SuppressWarnings("unchecked")
	default CustomCommand getCommand(CommandEvent event) throws Exception {
		Constructor<CustomCommand> constructor = (Constructor<CustomCommand>) event.getCommand().getClass()
				.getDeclaredConstructor(CommandEvent.class);
		constructor.setAccessible(true);
		return constructor.newInstance(event);
	}

	default Set<Method> getPathMethods(CustomCommand command) {
		Set<Method> methods = getMethods(command.getClass(), withAnnotation(Path.class));
		if (methods.size() == 1)
			return Collections.singleton(methods.iterator().next());
		return methods;
	}

	default Method getMethod(CustomCommand command, List<String> args) {
		Method method = new PathParser(getPathMethods(command)).match(args);

		// Work backwards until match is found - not needed after rework?
//		int i = args.size() - 1;
//		while (method == null && i >= 0) {
//			method = getPathMethod(command, args.subList(0, i));
//			--i;
//		}

		if (method == null)
			// TODO No default path, what do?
			throw new InvalidInputException("No matching path");

		return method;
	}

	default boolean hasPermission(CommandSender sender, Method method) {
		String permission = getPermission();
		if (permission != null && !sender.hasPermission(permission))
			return false;

		if (method.isAnnotationPresent(Permission.class)) {
			Permission pathPermission = method.getAnnotation(Permission.class);
			permission = pathPermission.absolute() ? "" : (permission + ".") + pathPermission.value();
			if (!sender.hasPermission(permission))
				return false;
		}

		return true;
	}

}


