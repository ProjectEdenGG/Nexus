package me.pugabyte.bncore.models.commands.models;

import me.pugabyte.bncore.models.commands.models.annotations.Aliases;
import me.pugabyte.bncore.models.commands.models.annotations.Arg;
import me.pugabyte.bncore.models.commands.models.annotations.Path;
import me.pugabyte.bncore.models.commands.models.annotations.Permission;
import me.pugabyte.bncore.models.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.commands.models.events.TabEvent;
import me.pugabyte.bncore.models.exceptions.BNException;
import me.pugabyte.bncore.models.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.models.exceptions.preconfigured.NoPermissionException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static me.pugabyte.bncore.BNCore.*;
import static org.reflections.ReflectionUtils.getMethods;
import static org.reflections.ReflectionUtils.withAnnotation;

@SuppressWarnings("unused")
public interface ICustomCommand {

	default void execute(CommandEvent event) {
		try {
			CustomCommand command = getCommand(event);
			Method method = getMethod(command, event.getArgs());
			checkPermission(event.getSender(), method);
			command.invoke(method, event.getArgs());
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

	default void invoke(Method method, List<String> args) throws Exception {
		List<Parameter> parameters = Arrays.asList(method.getParameters());
		Iterator<String> path = Arrays.asList(method.getAnnotation(Path.class).value().split(" ")).iterator();
		Object[] objects = new Object[parameters.size()];

		int i = 1;
		int pathIndex = 0;
		for (Parameter parameter : parameters) {
			// log("Parameter: " + parameter.getName() + " (" + parameter.getType().getName() + ")");
			Arg annotation = parameter.getDeclaredAnnotation(Arg.class);
			if (annotation == null)
				throw new BNException("Command parameter not annotated with @Argument: "
						+ method.getName() + "(" + parameter.getType().getName() + " " + parameter.getName() + ")");

			String pathArg = "";
			while (!pathArg.startsWith("{")) {
				pathArg = path.next();
				++pathIndex;
			}
			// log("  Path arg: " + parameter.getName() + " (Index: " + pathIndex + ")");

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
					value = String.join(" ", args.subList(pathIndex, args.size()));
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

	default Object convert(String value, Class<?> type){
		try {
			if (Player.class == type) return getPlayer(value);
			if (Boolean.class == type || Boolean.TYPE == type) return Boolean.parseBoolean(value);
			if (Integer.class == type || Integer.TYPE == type) return Integer.parseInt(value);
			if (Double.class == type || Double.TYPE == type) return Double.parseDouble(value);
			if (Float.class == type || Float.TYPE == type) return Float.parseFloat(value);
			if (Short.class == type || Short.TYPE == type) return Short.parseShort(value);
			if (Long.class == type || Long.TYPE == type) return Long.parseLong(value);
			if (Byte.class == type || Byte.TYPE == type) return Byte.parseByte(value);
			return value;
		} catch (Exception ex) {
			throw new BNException(ex.getCause().getClass().getName() + ": " + ex.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	default CustomCommand getCommand(CommandEvent event) throws Exception {
		Constructor<CustomCommand> constructor = (Constructor<CustomCommand>) event.getCommand().getClass()
				.getDeclaredConstructor(CommandEvent.class);
		constructor.setAccessible(true);
		return constructor.newInstance(event);
	}

	default Method getMethod(CustomCommand command, List<String> args) {
		Method method = getPathMethod(command, args);

		int i = args.size() - 1;
		while (method == null && i >= 0) {
			method = getPathMethod(command, args.subList(0, i));
			--i;
		}

		if (method == null)
			throw new InvalidInputException("No matching method"); // TODO

		return method;
	}

	@SuppressWarnings("unchecked")
	default Method getPathMethod(CustomCommand command, List<String> args) {
		for (Method method : getMethods(command.getClass(), withAnnotation(Path.class))) {
			if (new PathParser(method.getAnnotation(Path.class)).matches(args))
				return method;
		}
		return null;
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


