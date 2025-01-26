package gg.projecteden.nexus.framework.commands.models;

import gg.projecteden.api.common.exceptions.EdenException;
import gg.projecteden.api.mongodb.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commands.Commands;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.commands.models.events.CommandTabEvent;
import gg.projecteden.nexus.framework.exceptions.NexusException;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.bukkit.OfflinePlayer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Data
class PathParser {
	@NonNull
	private final CommandEvent event;
	private final CustomCommand command;
	private final List<Method> methods;

	public PathParser(@NonNull CommandEvent event) {
		this.event = event;
		this.command = event.getCommand();
		this.methods = Utils.reverse(command.getPathMethodsForExecution(event));
	}

	@Data
	class TabCompleteHelper {
		private Method method;
		private List<String> pathArgs;
		private List<String> realArgs;
		private List<TabCompleteArg> args = new ArrayList<>();
		private Class<?> finalType;
		private Method finalTabCompleter;
		private Object finalContextArg;

		public TabCompleteHelper(Method method, List<String> realArgs) {
			this.method = method;
			this.realArgs = realArgs;
			this.pathArgs = Arrays.asList(method.getAnnotation(Path.class).value().split(" "));

			createArgs();
		}

		private void createArgs() {
			int index = 0;
			int paramIndex = 0;
			for (String realArg : new ArrayList<>(realArgs)) {
				TabCompleteArg arg = new TabCompleteArg(method, realArg);
				if (pathArgs.size() > index)
					if (!pathArgs.get(index).startsWith("[-"))
						arg.setPathArg(pathArgs.get(index));
				if (realArgs.size() == index + 1)
					arg.isCompletionIndex(true);

				if (arg.isVariable()) {
					arg.setParamIndex(paramIndex++);
					Parameter parameter = method.getParameters()[arg.getParamIndex()];
					Arg annotation = parameter.getAnnotation(Arg.class);
					if (annotation != null && !Nullables.isNullOrEmpty(annotation.permission()))
						if (!event.getSender().hasPermission(annotation.permission()))
							break;

					arg.setTabCompleter(parameter.getType());
					arg.setList(Collection.class.isAssignableFrom(parameter.getType()));
					if (annotation != null) {
						if (annotation.type() != void.class)
							arg.setTabCompleter(annotation.type());
						if (annotation.tabCompleter() != void.class)
							arg.setTabCompleter(annotation.tabCompleter());
						if (annotation.context() > 0)
							try {
								arg.setContextArg(command.getMethodParameters(method, event, false)[annotation.context() - 1]);
							} catch (Exception ex) {
								if (Nexus.isDebug())
									if (!(ex instanceof InvocationTargetException && ex.getCause() instanceof EdenException))
										ex.printStackTrace();
							}
					}

					if (PlayerOwnedObject.class.isAssignableFrom(arg.getType()) && arg.getTabCompleter() == null)
						arg.setTabCompleter(OfflinePlayer.class);

					if (finalTabCompleter == null) {
						if (arg.getPathArg() != null && arg.getPathArg().contains("...")) {
							finalType = arg.getType();
							finalTabCompleter = arg.getTabCompleter();
							finalContextArg = arg.getContextArg();
						}
					}
				}

				if (finalType != null)
					arg.setType(finalType);
				if (finalTabCompleter != null)
					arg.setTabCompleter(finalTabCompleter);
				if (finalContextArg != null)
					arg.setContextArg(finalContextArg);

				args.add(arg);
				++index;
			}
		}

		@ToString.Include
		boolean pathMatches() {
			for (TabCompleteArg arg : args)
				if (!arg.matches())
					return false;

			return true;
		}

		@ToString.Include
		List<String> tabComplete() {
			ArrayList<String> completions = new ArrayList<>();
			for (TabCompleteArg arg : args)
				if (arg.isCompletionIndex())
					completions.addAll(arg.tabComplete());

			completions.addAll(getSwitches());

			return completions;
		}

		private List<String> getSwitches() {
			List<String> switches = new ArrayList<>();
			String lastArg = realArgs.get(realArgs.size() - 1);

			if (!lastArg.startsWith("-"))
				return switches;

			for (Parameter parameter : method.getParameters()) {
				Arg argAnnotation = parameter.getDeclaredAnnotation(Arg.class);
				Switch switchAnnotation = parameter.getDeclaredAnnotation(Switch.class);
				if (switchAnnotation == null)
					continue;

				if (argAnnotation != null)
					if (!Nullables.isNullOrEmpty(argAnnotation.permission()))
						if (!event.getSender().hasPermission(argAnnotation.permission()))
							continue;

				Pattern pattern = CustomCommand.getSwitchPattern(parameter);
				boolean found = false;
				for (String arg : event.getArgs()) {
					Matcher matcher = pattern.matcher(arg);

					if (matcher.find())
						found = true;
				}

				if (!found) {
					switches.add("--" + parameter.getName());
					if (switchAnnotation.shorthand() != '-')
						switches.add("-" + switchAnnotation.shorthand());
				}

				if (lastArg.contains("=")) {
					TabCompleteArg arg = new TabCompleteArg(method, lastArg.split("=", 2)[1]);
					arg.setPathArg("[switch]");
					arg.isCompletionIndex(true);

					arg.setTabCompleter(parameter.getType());
					arg.setList(Collection.class.isAssignableFrom(parameter.getType()));

					if (argAnnotation != null) {
						if (!Nullables.isNullOrEmpty(argAnnotation.permission()))
							if (!event.getSender().hasPermission(argAnnotation.permission()))
								break;

						if (argAnnotation.type() != void.class)
							arg.setTabCompleter(argAnnotation.type());
						if (argAnnotation.tabCompleter() != void.class)
							arg.setTabCompleter(argAnnotation.tabCompleter());
					}

					if (PlayerOwnedObject.class.isAssignableFrom(arg.getType()) && arg.getTabCompleter() == null)
						arg.setTabCompleter(OfflinePlayer.class);

					final boolean matchesLong = lastArg.toLowerCase().startsWith("--" + parameter.getName().toLowerCase() + "=");
					final boolean matchesShort = switchAnnotation.shorthand() != '-' && lastArg.toLowerCase().startsWith("-" + switchAnnotation.shorthand() + "=");
					if (matchesLong || matchesShort)
						switches.addAll(arg.tabComplete().stream().map(completion -> lastArg.split("=")[0] + "=" + completion).collect(Collectors.toList()));
				}
			}

			return switches.stream().filter(completion -> completion.toLowerCase().startsWith(lastArg.toLowerCase())).collect(Collectors.toList());
		}

	}

	@Data
	@AllArgsConstructor
	@RequiredArgsConstructor
	class TabCompleteArg {
		@NonNull
		private Method method;
		@NonNull
		private String realArg;
		private String pathArg;
		@Accessors(fluent = true)
		private boolean isCompletionIndex = false;
		private Integer paramIndex;

		private Class<?> type;
		private boolean isList;
		private Method tabCompleter;
		private Object contextArg;

		@ToString.Include
		boolean isLiteral() {
			if (pathArg == null) return false;
			return !pathArg.startsWith("[") && !pathArg.startsWith("<");
		}

		@ToString.Include
		boolean isVariable() {
			if (pathArg == null) return false;
			return !isLiteral();
		}

		@ToString.Include
		boolean matches() {
			if (Nullables.isNullOrEmpty(pathArg) || isVariable())
				return true;

			if (isCompletionIndex)
				if (Nullables.isNullOrEmpty(realArg))
					return true;
				else
					if (isLiteral())
						return getSplitPathArg(realArg).size() > 0;

			for (String option : tabComplete())
				if (option.equalsIgnoreCase(realArg))
					return true;

			return false;
		}

		private List<String> getSplitPathArg(String filter) {
			List<String> options = Arrays.asList(pathArg.replaceAll("\\(", "").replaceAll("\\)", "").split("\\|"));
			if (filter != null)
				options = options.stream().filter(option -> option.toLowerCase().startsWith(filter.toLowerCase())).collect(Collectors.toList());
			return options;
		}

		@ToString.Include
		@SneakyThrows
		List<String> tabComplete() {
			if (isLiteral())
				return getSplitPathArg(realArg);

			String realArg = this.realArg;
			List<String> results = new ArrayList<>();

			if (isList) {
				if (realArg.lastIndexOf(",") == realArg.length() - 1)
					realArg = "";
				else {
					String[] split = realArg.split(StringUtils.COMMA_SPLIT_REGEX);
					realArg = split[split.length - 1];
				}
			}

			if (tabCompleter != null) {
				CustomCommand tabCompleteCommand = command;
				if (!(tabCompleter.getDeclaringClass().equals(command.getClass()) || Modifier.isAbstract(tabCompleter.getDeclaringClass().getModifiers())))
					tabCompleteCommand = command.getNewCommand(command.getEvent(), tabCompleter.getDeclaringClass());

				if (tabCompleter.getParameterCount() == 1) {
					results.addAll((List<String>) tabCompleter.invoke(tabCompleteCommand, realArg.toLowerCase()));
				} else if (tabCompleter.getParameterCount() == 2)
					results.addAll((List<String>) tabCompleter.invoke(tabCompleteCommand, realArg.toLowerCase(), contextArg));
				else
					throw new NexusException("Unknown converter parameters in " + tabCompleter.getName());
			} else if (type != null && type.isEnum())
				results.addAll(command.tabCompleteEnum(realArg.toLowerCase(), (Class<? extends Enum<?>>) type));

			if (isList) {
				List<String> realArgs = new ArrayList<>(Arrays.asList(this.realArg.split(StringUtils.COMMA_SPLIT_REGEX)));
				if (!this.realArg.endsWith(","))
					realArgs.remove(realArgs.size() - 1);
				String realArgBeginning = String.join(",", realArgs);
				if (realArgs.size() > 0)
					realArgBeginning += ",";

				ArrayList<String> strings = new ArrayList<>(results);
				results.clear();
				for (String result : strings)
					results.add(realArgBeginning + result);
			}

			return results;
		}

		void setTabCompleter(Method tabCompleter) {
			this.tabCompleter = tabCompleter;
		}

		void setTabCompleter(Class<?> clazz) {
			this.type = clazz;
			if (Commands.getTabCompleters().containsKey(clazz))
				this.tabCompleter = Commands.getTabCompleters().get(clazz);
			if (this.tabCompleter == null && clazz == Boolean.TYPE)
				this.tabCompleter = Commands.getTabCompleters().get(Boolean.class);
		}

	}

	List<String> tabComplete(CommandTabEvent event) {
		List<String> completions = new ArrayList<>();

		for (Method method : methods) {
			if (!event.getCommand().hasPermission(event.getSender(), method))
				continue;
			TabCompleteIgnore tabCompleteIgnore = method.getAnnotation(TabCompleteIgnore.class);
			if (tabCompleteIgnore != null)
				if (Nullables.isNullOrEmpty(tabCompleteIgnore.permission()) || !event.getSender().hasPermission(tabCompleteIgnore.permission()))
					continue;

			TabCompleteHelper helper = new TabCompleteHelper(method, event.getArgs());
//			Nexus.log(helper.toString());
			if (!helper.pathMatches())
				continue;

			completions.addAll(helper.tabComplete());
		}

		return completions.stream().distinct().collect(Collectors.toList());
	}

	Method match(List<String> args) {
		String argsString = String.join(" ", args).toLowerCase();

		// Look for exact match
		for (Method method : methods)
			if (method.getAnnotation(Path.class).value().equalsIgnoreCase(argsString))
				return method;

		Method fallback = null;

		for (Method method : methods) {
			String path = getPathString(method);
			String literalWords = getLiteralWords(path);

			if (literalWords.length() == 0) {
				if (args.size() > 0 && path.length() > 0)
					if (path.split(" ").length <= args.size())
						return method;

				if (args.size() == 0 && path.length() == 0)
					return method;

				if (fallback == null) {
					if (args.size() >= getRequiredArgs(path))
						fallback = method;
					else if (args.size() == 0)
						fallback = method;
				} else if (args.size() == 0 && getPathString(method).length() < getPathString(fallback).length())
					fallback = method;
				continue;
			}

			// Has arguments, has literal worlds
			Matcher matcher = Pattern.compile("^" + literalWords + " .*").matcher(argsString + " ");
			if (matcher.matches())
				return method;
		}

		return fallback;
	}

	protected static String getPathString(Method method) {
		return method.getAnnotation(Path.class).value().toLowerCase();
	}

	protected static String getLiteralWords(String path) {
		String[] pathArgs = path.split(" ");

		String literalWords = "";
		if (path.length() > 0)
			pathArgLoop: for (String pathArg : pathArgs)
				switch (StringUtils.left(pathArg, 1)) {
					case "[":
					case "<":
						break pathArgLoop;
					default:
						literalWords += pathArg + " ";
				}

		literalWords = literalWords.trim().toLowerCase();
		return literalWords;
	}

	protected static int getRequiredArgs(String path) {
		String[] pathArgs = path.split(" ");

		int requiredArgs = 0;
		if (path.length() > 0)
			for (String pathArg : pathArgs)
				if (!pathArg.startsWith("["))
					++requiredArgs;

		return requiredArgs;
	}

}
