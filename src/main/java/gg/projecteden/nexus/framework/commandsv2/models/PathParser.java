package gg.projecteden.nexus.framework.commandsv2.models;

import gg.projecteden.api.common.exceptions.EdenException;
import gg.projecteden.api.mongodb.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commandsv2.Commands;
import gg.projecteden.nexus.framework.commandsv2.annotations.OldArg;
import gg.projecteden.nexus.framework.commandsv2.annotations.Path;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Switch;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.events.CommandTabEvent;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMeta;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMeta.PathMeta;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMeta.PathMeta.ArgumentMeta;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMeta.PathMeta.LiteralArgumentMeta;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMeta.PathMeta.VariableArgumentMeta;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMetaInstance.PathMetaInstance;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMetaInstance.PathMetaInstance.ArgumentMetaInstance;
import gg.projecteden.nexus.framework.exceptions.NexusException;
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
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.framework.commands.models.CustomCommand.getSwitchPattern;
import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.nexus.utils.StringUtils.COMMA_SPLIT_REGEX;
import static gg.projecteden.nexus.utils.StringUtils.left;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Data
public
class PathParser {
	@NonNull
	private final CommandEvent event;
	private final CustomCommand command;
	private final List<CustomCommandMeta.PathMeta> paths;

	public PathParser(@NonNull CommandEvent event) {
		this.event = event;
		this.command = event.getCommandMetaInstance().getCommandMeta().getInstance();
		this.paths = Utils.reverse(command.getPathMethodsForExecution(event));
	}

	@Data
	class TabCompleteHelper {
		private PathMetaInstance pathMetaInstance;
		private List<TabCompleteArg> args = new ArrayList<>();
		private Class<?> finalType;
		private Method finalTabCompleter;
		private Object finalContextArg;

		public TabCompleteHelper(PathMeta pathMeta, List<String> realArgs) {
			createArgs();
		}

		private void createArgs() {
			int index = 0;
			int paramIndex = 0;
			for (ArgumentMetaInstance realArg : pathMetaInstance.getArgumentMetaInstances()) {

				TabCompleteArg arg = new TabCompleteArg(pathMeta, realArg);
				if (pathArgs.size() > index)
					if (!pathArgs.get(index).startsWith("[-"))
						arg.setPathArg(pathArgs.get(index));
				if (realArgs.size() == index + 1)
					arg.isCompletionIndex(true);

				if (realArg.getArgumentMeta() instanceof VariableArgumentMeta variableArgumentMeta) {
					arg.setParamIndex(paramIndex++);
					Parameter parameter = pathMeta.getParameters()[arg.getParamIndex()];
					if (annotation != null && !isNullOrEmpty(annotation.permission()))
						if (!event.getSender().hasPermission(annotation.permission()))
							break;

					arg.setTabCompleter(variableArgumentMeta.getType());
					arg.setList(Collection.class.isAssignableFrom(parameter.getType()));
					if (variableArgumentMeta.getErasureType() != null)
						arg.setTabCompleter(variableArgumentMeta.getErasureType());
					if (variableArgumentMeta.getTabCompleter() != null)
						arg.setTabCompleter(variableArgumentMeta.getTabCompleter());
					if (variableArgumentMeta.getContext() > 0)
						try {
							arg.setContextArg(command.getMethodParameters(pathMeta, event, false)[annotation.context() - 1]);
						} catch (Exception ex) {
							if (Nexus.isDebug())
								if (!(ex instanceof InvocationTargetException && ex.getCause() instanceof EdenException))
									ex.printStackTrace();
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

			for (Parameter parameter : pathMeta.getParameters()) {
				OldArg argAnnotation = parameter.getDeclaredAnnotation(OldArg.class);
				Switch switchAnnotation = parameter.getDeclaredAnnotation(Switch.class);
				if (switchAnnotation == null)
					continue;

				Pattern pattern = getSwitchPattern(parameter);
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
					TabCompleteArg arg = new TabCompleteArg(pathMeta, lastArg.split("=", 2)[1]);
					arg.setPathArg("[switch]");
					arg.isCompletionIndex(true);

					arg.setTabCompleter(parameter.getType());
					arg.setList(Collection.class.isAssignableFrom(parameter.getType()));

					if (argAnnotation != null) {
						if (!isNullOrEmpty(argAnnotation.permission()))
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
						switches.addAll(arg.tabComplete().stream().map(completion -> lastArg.split("=")[0] + "=" + completion).collect(toList()));
				}
			}

			return switches.stream().filter(completion -> completion.toLowerCase().startsWith(lastArg.toLowerCase())).collect(toList());
		}

	}

	@Data
	@AllArgsConstructor
	@RequiredArgsConstructor
	class TabCompleteArg {
		@NonNull
		private ArgumentMetaInstance realArg;
		private ArgumentMeta pathArg;
		@Accessors(fluent = true)
		private boolean isCompletionIndex = false;
		private Integer paramIndex;

		private Class<?> type;
		private boolean isList;
		private Method tabCompleter;
		private Object contextArg;

		@ToString.Include
		boolean isLiteral() {
			return pathArg instanceof LiteralArgumentMeta;
		}

		@ToString.Include
		boolean isVariable() {
			return pathArg instanceof VariableArgumentMeta;
		}

		@ToString.Include
		boolean matches() {
			if (isVariable())
				return true;

			if (isCompletionIndex)
				if (isNullOrEmpty(realArg.getInput()))
					return true;
				else
					if (isLiteral())
						return pathArg.getName().toLowerCase().startsWith(realArg.getInput().toLowerCase());

			for (String option : tabComplete())
				if (option.equalsIgnoreCase(realArg.getInput()))
					return true;

			return false;
		}

		@ToString.Include
		@SneakyThrows
		List<String> tabComplete() {
			if (isLiteral()) {
				if (pathArg.getName().toLowerCase().startsWith(realArg.getInput().toLowerCase()))
					return Collections.singletonList(pathArg.getName());

				return emptyList();
			}

			String input = this.realArg.getInput();
			List<String> results = new ArrayList<>();

			if (isList) {
				if (input.lastIndexOf(",") == input.length() - 1)
					input = "";
				else {
					String[] split = input.split(COMMA_SPLIT_REGEX);
					input = split[split.length - 1];
				}
			}

			if (tabCompleter != null) {
				CustomCommand tabCompleteCommand = command;
				if (!(tabCompleter.getDeclaringClass().equals(command.getClass()) || Modifier.isAbstract(tabCompleter.getDeclaringClass().getModifiers())))
					tabCompleteCommand = command.getNewCommand(command.getEvent(), tabCompleter.getDeclaringClass());

				if (tabCompleter.getParameterCount() == 1) {
					results.addAll((List<String>) tabCompleter.invoke(tabCompleteCommand, input.toLowerCase()));
				} else if (tabCompleter.getParameterCount() == 2)
					results.addAll((List<String>) tabCompleter.invoke(tabCompleteCommand, input.toLowerCase(), contextArg));
				else
					throw new NexusException("Unknown converter parameters in " + tabCompleter.getName());
			} else if (type != null && type.isEnum())
				results.addAll(command.tabCompleteEnum(input.toLowerCase(), (Class<? extends Enum<?>>) type));

			if (isList) {
				List<String> realArgs = new ArrayList<>(Arrays.asList(input.split(COMMA_SPLIT_REGEX)));
				if (!input.endsWith(","))
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
			if (Commands.TAB_COMPLETERS.containsKey(clazz))
				this.tabCompleter = Commands.TAB_COMPLETERS.get(clazz);
			if (this.tabCompleter == null && clazz == Boolean.TYPE)
				this.tabCompleter = Commands.TAB_COMPLETERS.get(Boolean.class);
		}

	}

	List<String> tabComplete(CommandTabEvent event) {
		List<String> completions = new ArrayList<>();

		for (PathMeta pathMeta : event.getCommandMetaInstance().getCommandMeta().getPaths()) {
			if (!event.getCommandMetaInstance().getCommandMeta().getInstance().hasPermission(event, pathMeta))
				continue;

			if (pathMeta.isIgnoreTabComplete())
				if (!isNullOrEmpty(pathMeta.getIgnoreTabCompleteBypass()) && !event.getSender().hasPermission(pathMeta.getIgnoreTabCompleteBypass()))
					continue;

			TabCompleteHelper helper = new TabCompleteHelper(pathMeta, event.getArgs());
//			Nexus.log(helper.toString());
			if (!helper.pathMatches())
				continue;

			completions.addAll(helper.tabComplete());
		}

		return completions.stream().distinct().collect(toList());
	}

	public static PathMeta match(CustomCommandMeta commandMeta, List<String> args) {
		String argsString = String.join(" ", args);

		// Look for exact match
		for (PathMeta pathMeta : commandMeta.getPaths())
			if (pathMeta.getUsage().equalsIgnoreCase(argsString))
				return pathMeta;

		PathMeta fallback = null;

		for (PathMeta pathMeta : commandMeta.getPaths()) {
			String path = pathMeta.getUsage();
			String literalWords = String.join(" ", pathMeta.getLiterals().stream().map(ArgumentMeta::getName).toList());

			if (literalWords.length() == 0) {
				if (args.size() > 0 && path.length() > 0)
					if (path.split(" ").length <= args.size())
						return pathMeta;

				if (args.size() == 0 && path.length() == 0)
					return pathMeta;

				if (fallback == null) {
					if (args.size() >= pathMeta.getRequired().size())
						fallback = pathMeta;
					else if (args.size() == 0)
						fallback = pathMeta;
				} else if (args.size() == 0 && pathMeta.getUsage().length() < fallback.getUsage().length())
					fallback = pathMeta;
				continue;
			}

			// Has arguments, has literal worlds
			Matcher matcher = Pattern.compile("^" + literalWords + " .*").matcher(argsString + " ");
			if (matcher.matches())
				return pathMeta;
		}

		return fallback;
	}


}
