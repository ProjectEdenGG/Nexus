package me.pugabyte.bncore.framework.commands.models;

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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static me.pugabyte.bncore.Utils.listLast;
import static me.pugabyte.bncore.Utils.right;
import static org.reflections.ReflectionUtils.getMethods;
import static org.reflections.ReflectionUtils.withAnnotation;

@SuppressWarnings("unused")
public interface ICustomCommand {

	default void execute(CommandEvent event) {
		try {
			CustomCommand command = getCommand(event);
			event.setCommand(command);
			Method method = getMethod(command, event.getArgs());
			checkPermission(event.getSender(), method);
			command.invoke(method, event);
		} catch (Exception ex) {
			event.handleException(ex);
		}
	}

	default List<String> tabComplete() {
		return null;
	}

	default List<String> tab(TabEvent event) {
		String permission = getPermission();
		if (permission != null && !event.getSender().hasPermission(permission))
			return null;

		return tabComplete();
	}

	default String getName() {
		return getAliases()[0];
	}

	default String[] getAliases() {
		for (Annotation annotation : this.getClass().getAnnotations()) {
			if (annotation instanceof Aliases) {
				return ((Aliases) annotation).value();
			}
		}

		throw new RuntimeException("No aliases configured for command " + this.getClass().getName());
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
			// log("Parameter: " + parameter.getName() + " (" + parameter.getType().getName() + ")");
			Arg annotation = parameter.getDeclaredAnnotation(Arg.class);
			if (annotation == null)
				throw new BNException("Command parameter not annotated with @Arg: "
						+ method.getName() + "(" + parameter.getType().getName() + " " + parameter.getName() + ")");

			String pathArg = "";
			while (!pathArg.startsWith("{")) {
				pathArg = path.next();
				++pathIndex;
			}
			// log("  Path arg: " + parameter.getName() + " (Index: " + pathIndex + ")");

			// TODO: This should be run on register
			String paramArgType = listLast(parameter.getType().getName(), ".").toLowerCase();
			String pathArgType = pathArg.replaceAll("[{}.]", "").toLowerCase();
			if (!paramArgType.equals(pathArgType))
				throw new BNException("Command parameter type does not match path type at parameter #"
						+ (Integer.parseInt(right(parameter.getName(), 1)) + 1)
						+ " (Param: " + paramArgType + ", Path: " + pathArgType + ")");

			// log("  Args size: " + args.size());
			String value = annotation.value();
			// log("  Defaulting to: " + value);
			if (args.size() >= pathIndex) {
				if (pathArg.contains("..."))
					value = String.join(" ", args.subList(pathIndex - 1, args.size()));
				else
					value = args.get(pathIndex - 1);
				// log("  Overriding with: " + value);
			}

			objects[i - 1] = convert(value, parameter.getType());
			++i;
		}

		method.setAccessible(true);
		method.invoke(this, objects);
	}

	// TODO: Make more abstract
	Object convert(String value, Class<?> type);

	@SuppressWarnings("unchecked")
	default CustomCommand getCommand(CommandEvent event) throws Exception {
		Constructor<CustomCommand> constructor = (Constructor<CustomCommand>) event.getCommand().getClass()
				.getDeclaredConstructor(CommandEvent.class);
		constructor.setAccessible(true);
		return constructor.newInstance(event);
	}

	default Method getMethod(CustomCommand command, List<String> args) {
		Method method = getPathMethod(command, args);

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

	@SuppressWarnings("unchecked")
	default Method getPathMethod(CustomCommand command, List<String> args) {
		Set<Method> methods = getMethods(command.getClass(), withAnnotation(Path.class));
		if (methods.size() == 1)
			return methods.iterator().next();
		return new PathParser(methods).match(args);
	}

	default void checkPermission(CommandSender sender, Method method) {
		String permission = getPermission();
		if (permission != null && !sender.hasPermission(permission))
			throw new NoPermissionException();

		if (method.isAnnotationPresent(Permission.class)) {
			Permission pathPermission = method.getAnnotation(Permission.class);
			permission = pathPermission.absolute() ? "" : (permission + ".") + pathPermission.value();
			if (!sender.hasPermission(permission))
				throw new NoPermissionException();
		}
	}

}


