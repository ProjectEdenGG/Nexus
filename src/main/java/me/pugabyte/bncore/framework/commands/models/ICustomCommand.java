package me.pugabyte.bncore.framework.commands.models;

import com.google.common.base.Strings;
import lombok.SneakyThrows;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.Commands;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Async;
import me.pugabyte.bncore.framework.commands.models.annotations.Cooldown;
import me.pugabyte.bncore.framework.commands.models.annotations.Cooldown.Part;
import me.pugabyte.bncore.framework.commands.models.annotations.Fallback;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.commands.models.events.TabEvent;
import me.pugabyte.bncore.framework.exceptions.BNException;
import me.pugabyte.bncore.framework.exceptions.postconfigured.CommandCooldownException;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.framework.exceptions.postconfigured.PlayerNotFoundException;
import me.pugabyte.bncore.framework.exceptions.postconfigured.PlayerNotOnlineException;
import me.pugabyte.bncore.framework.exceptions.preconfigured.MissingArgumentException;
import me.pugabyte.bncore.framework.exceptions.preconfigured.NoPermissionException;
import me.pugabyte.bncore.models.cooldown.CooldownService;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.objenesis.ObjenesisStd;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.framework.commands.models.PathParser.getLiteralWords;
import static me.pugabyte.bncore.framework.commands.models.PathParser.getPathString;
import static me.pugabyte.bncore.utils.StringUtils.listLast;
import static org.reflections.ReflectionUtils.getAllMethods;
import static org.reflections.ReflectionUtils.withAnnotation;

@SuppressWarnings("unused")
public abstract class ICustomCommand {

	public void execute(CommandEvent event) {
		try {
			CustomCommand command = getCommand(event);
			Method method = getMethod(event);
			if (method == null)
				return;
			event.setUsage(method);
			if (!hasPermission(event.getSender(), method))
				throw new NoPermissionException();
			checkCooldown(command);
			command.invoke(method, event);
		} catch (Exception ex) {
			event.handleException(ex);
		}
	}

	public List<String> tabComplete(TabEvent event) {
		try {
			getCommand(event);
			return new PathParser(event).tabComplete(event);
		} catch (Exception ex) {
			event.handleException(ex);
		}
		return new ArrayList<>();
	}

	public String getName() {
		return listLast(this.getClass().toString(), ".").replaceAll("Command$", "");
	}

	public List<String> getAliases() {
		List<String> aliases = new ArrayList<>();

		for (Annotation annotation : this.getClass().getAnnotations()) {
			if (annotation instanceof Aliases) {
				for (String alias : ((Aliases) annotation).value()) {
					if (!Pattern.compile("[a-zA-Z0-9_-]+").matcher(alias).matches()) {
						BNCore.warn("Alias invalid: " + getName() + "Command.java / " + alias);
						continue;
					}

					aliases.add(alias);
				}
			}
		}

		return aliases;
	}

	public List<String> getAllAliases() {
		List<String> aliases = getAliases();
		aliases.add(getName());
		return aliases;
	}

	private String _getPermission() {
		if (this.getClass().getAnnotation(Permission.class) != null)
			return this.getClass().getAnnotation(Permission.class).value();
		return null;
	}

	protected void invoke(Method method, CommandEvent event) {
		Runnable run = () -> {
			try {
				Object[] objects = getMethodParameters(method, event, true);
				method.setAccessible(true);
				method.invoke(this, objects);
			} catch (Exception ex) {
				event.handleException(ex);
			}
		};

		if (method.getAnnotation(Async.class) != null)
			Tasks.async(run);
		else
			run.run();
	}

	Object[] getMethodParameters(Method method, CommandEvent event, boolean doValidation) {
		List<String> args = event.getArgs();
		List<Parameter> parameters = Arrays.asList(method.getParameters());
		String pathValue = method.getAnnotation(Path.class).value();
		Iterator<String> path = Arrays.asList(pathValue.split(" ")).iterator();
		Object[] objects = new Object[parameters.size()];

		// TODO: Validate params and path have same args

		int i = 1;
		int pathIndex = 0;
		for (Parameter parameter : parameters) {
			String pathArg = "";
			while (!pathArg.startsWith("{") && !pathArg.startsWith("[") && !pathArg.startsWith("<") && path.hasNext()) {
				pathArg = path.next();
				++pathIndex;
			}

			Arg annotation = parameter.getDeclaredAnnotation(Arg.class);
			String value = (annotation == null ? null : annotation.value());
			int contextArgIndex = (annotation == null ? -1 : annotation.contextArg());
			Object contextArg = (contextArgIndex > 0 && objects.length >= contextArgIndex) ? objects[contextArgIndex - 1] : null;

			if (args.size() >= pathIndex) {
				if (pathArg.contains("..."))
					value = String.join(" ", args.subList(pathIndex - 1, args.size()));
				else
					value = args.get(pathIndex - 1);
			}

			boolean required = doValidation && (pathArg.startsWith("<") || (pathArg.startsWith("[") && !Strings.isNullOrEmpty(value)));
			try {
				objects[i - 1] = convert(value, contextArg, parameter.getType(), annotation, event, required);
			} catch (MissingArgumentException ex) {
				event.getCommand().showUsage();
			}
			++i;
		}
		return objects;
	}

	List<Class<? extends Exception>> conversionExceptions = Arrays.asList(
			InvalidInputException.class,
			PlayerNotFoundException.class,
			PlayerNotOnlineException.class
	);

	@SneakyThrows
	private Object convert(String value, Object context, Class<?> type, Arg annotation, CommandEvent event, boolean required) {
		if (Collection.class.isAssignableFrom(type)) {
			List<Object> values = new ArrayList<>();
			for (String index : value.split("[, ]"))
				values.add(convert(index, context, annotation.type(), annotation, event, required));
			values.removeIf(Objects::isNull);
			return values;
		}

		try {
			CustomCommand command = event.getCommand();
			if (Commands.getConverters().containsKey(type)) {
				Method converter = Commands.getConverters().get(type);
				boolean isAbstract = Modifier.isAbstract(converter.getDeclaringClass().getModifiers());
				if (!(isAbstract || converter.getDeclaringClass().equals(command.getClass()))) {
					command = getNewCommand(command.getEvent(), converter.getDeclaringClass());
				}
				if (converter.getParameterCount() == 1)
					return converter.invoke(command, value);
				else if (converter.getParameterCount() == 2)
					return converter.invoke(command, value, context);
				else
					throw new BNException("Unknown converter parameters in " + converter.getName());
			} else if (type.isEnum()) {
				return convertToEnum((Class<? extends Enum<?>>) type, value);
			}
		} catch (InvocationTargetException ex) {
			if (required)
				if (!Strings.isNullOrEmpty(value) && conversionExceptions.contains(ex.getCause().getClass()))
					throw ex;
				else
					throw new MissingArgumentException();
			else
				return null;
		}

		if (Strings.isNullOrEmpty(value))
			if (required)
				throw new MissingArgumentException();
			else
				if (isPrimitiveNumber(type))
					return 0;
				else
					return null;

		if (Boolean.class == type || Boolean.TYPE == type) {
			if (Arrays.asList("enable", "on", "yes", "1").contains(value)) value = "true";
			return Boolean.parseBoolean(value);
		}
		try {
			if (Integer.class == type || Integer.TYPE == type) return Integer.parseInt(value);
			if (Double.class == type || Double.TYPE == type) return Double.parseDouble(value);
			if (Float.class == type || Float.TYPE == type) return Float.parseFloat(value);
			if (Short.class == type || Short.TYPE == type) return Short.parseShort(value);
			if (Long.class == type || Long.TYPE == type) return Long.parseLong(value);
			if (Byte.class == type || Byte.TYPE == type) return Byte.parseByte(value);
		} catch (NumberFormatException ex) {
			throw new InvalidInputException("'" + value + "' is not a number");
		}
		return value;
	}

	private boolean isPrimitiveNumber(Class<?> type) {
		return Arrays.asList(Integer.TYPE, Double.TYPE, Float.TYPE, Short.TYPE, Long.TYPE, Byte.TYPE).contains(type);
	}

	@SneakyThrows
	private CustomCommand getCommand(CommandEvent event) {
		Constructor<? extends CustomCommand> constructor = event.getCommand().getClass().getDeclaredConstructor(CommandEvent.class);
		constructor.setAccessible(true);
		CustomCommand command = constructor.newInstance(event);
		event.setCommand(command);
		return command;
	}

	@SneakyThrows
	CustomCommand getNewCommand(CommandEvent originalEvent, Class<?> clazz) {
		CustomCommand customCommand = new ObjenesisStd().newInstance((Class<? extends CustomCommand>) clazz);
		CommandEvent newEvent = new CommandEvent(originalEvent.getSender(), customCommand, customCommand.getName(), new ArrayList<>());
		return getCommand(newEvent);
	}

	List<Method> getPathMethods() {
		ArrayList<Method> methods = new ArrayList<>(getAllMethods(this.getClass(), withAnnotation(Path.class)));

		methods.sort(
				Comparator.comparing(method ->
						Arrays.stream(getLiteralWords(getPathString((Method) method)).split(" "))
								.filter(string -> !Strings.isNullOrEmpty(string))
								.count())
				.thenComparing(method ->
						Arrays.stream(getPathString((Method) method).split(" "))
								.filter(string -> !Strings.isNullOrEmpty(string))
								.count()));

		return methods;
	}

	// TODO: Use same methods as tab complete
	private Method getMethod(CommandEvent event) {
		Method method = new PathParser(event).match(event.getArgs());

		if (method == null) {
			Fallback fallback = event.getCommand().getClass().getAnnotation(Fallback.class);
			if (fallback != null)
				Utils.runCommand(event.getSender(), fallback.value() + ":" + event.getAliasUsed() + " " + event.getArgsString());
			else if (!event.getArgsString().equalsIgnoreCase("help"))
				Utils.runCommand(event.getSender(), event.getAliasUsed() + " help");
			else
				throw new InvalidInputException("No matching path");
		}

		return method;
	}

	boolean hasPermission(CommandSender sender, Method method) {
		String permission = _getPermission();
		if (permission != null && !sender.hasPermission(permission))
			return false;

		if (method.isAnnotationPresent(Permission.class)) {
			Permission pathPermission = method.getAnnotation(Permission.class);
			if (permission != null)
				permission = pathPermission.absolute() ? "" : (permission + ".") + pathPermission.value();
			else
				permission = pathPermission.value();
			return sender.hasPermission(permission);
		}

		return true;
	}

	private void checkCooldown(CustomCommand command) {
		checkCooldown(command, command.getClass().getAnnotation(Cooldown.class), command.getName());
		checkCooldown(command, command.getEvent().getMethod().getAnnotation(Cooldown.class), command.getName() + "#" + command.getEvent().getMethod().getName());
	}

	private void checkCooldown(CustomCommand command, Cooldown cooldown, String commandId) {
		if (cooldown != null) {
			boolean bypass = false;
			if (!(command.getEvent().getSender() instanceof Player))
				bypass = true;
			else if (cooldown.bypass().length() > 0 && command.getEvent().getPlayer().hasPermission(cooldown.bypass()))
				bypass = true;

			if (!bypass) {
				int ticks = 0;
				for (Part part : cooldown.value())
					ticks += part.value().get() * part.x();

				CooldownService service = new CooldownService();
				UUID uuid = cooldown.global() ? BNCore.getUUID0() : ((Player) command.getEvent().getSender()).getUniqueId();
				String type = "command:" + commandId;

				if (!service.check(uuid, type, ticks))
					throw new CommandCooldownException(uuid, type);
			}
		}
	}

	@SneakyThrows
	protected Enum<?> convertToEnum(Class<? extends Enum<?>> clazz, String filter) {
		if (filter == null) throw new InvocationTargetException(new BNException("Missing argument"));
		return Arrays.stream(clazz.getEnumConstants())
				.filter(value -> value.name().toLowerCase().startsWith(filter.toLowerCase()))
				.findFirst()
				.orElseThrow(() -> new InvalidInputException(clazz.getSimpleName() + " from " + filter + " not found"));
	}

	List<String> tabCompleteEnum(Class<? extends Enum<?>> clazz, String filter) {
		return Arrays.stream(clazz.getEnumConstants())
				.map(value -> value.name().toLowerCase())
				.filter(value -> value.toLowerCase().startsWith(filter.toLowerCase()))
				.collect(Collectors.toList());
	}

}


