package me.pugabyte.bncore.framework.commands.models;

import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.experimental.Accessors;
import me.pugabyte.bncore.framework.commands.Commands;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.commands.models.events.TabEvent;
import me.pugabyte.bncore.framework.exceptions.BNException;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.utils.StringUtils.left;

@Data
class PathParser {
	@NonNull
	CommandEvent event;
	CustomCommand command;
	List<Method> methods;

	public PathParser(@NonNull CommandEvent event) {
		this.event = event;
		this.command = event.getCommand();
		this.methods = new ArrayList<>(command.getPathMethods());

		// Sort by most literal words first (most specific first)
		methods.sort(Comparator.comparing(method -> getPathString(method).split(" ").length));
		Collections.reverse(methods);
	}

	@Data
	class TabCompleteHelper {
		private Method method;
		private List<String> pathArgs;
		private List<String> realArgs;
		private List<TabCompleteArg> args = new ArrayList<>();

		public TabCompleteHelper(Method method, List<String> realArgs) {
			this.method = method;
			this.realArgs = realArgs;
			this.pathArgs = Arrays.asList(method.getAnnotation(Path.class).value().split(" "));

			createArgs();
		}

		private void createArgs() {
			int index = 0;
			int paramIndex = 0;
			for (String realArg : realArgs) {
				TabCompleteArg arg = new TabCompleteArg(index, realArg);
				if (pathArgs.size() > index)
					arg.setPathArg(pathArgs.get(index));
				if (realArgs.size() == index + 1)
					arg.isCompletionIndex(true);

				if (arg.isVariable()) {
					arg.setParamIndex(paramIndex++);
					Parameter parameter = method.getParameters()[arg.getParamIndex()];
					arg.setTabCompleter(parameter.getType());
					if (parameter.getAnnotation(Arg.class) != null) {
						arg.setTabCompleter(parameter.getAnnotation(Arg.class).tabCompleter());
						if (parameter.getAnnotation(Arg.class).contextArg() > 0)
							arg.setContextArg(command.getMethodParameters(method, event, false)[parameter.getAnnotation(Arg.class).contextArg() - 1]);
					}
				}

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
			for (TabCompleteArg arg : args)
				if (arg.isCompletionIndex())
					return arg.tabComplete();

			return new ArrayList<>();
		}

	}

	@Data
	@AllArgsConstructor
	@RequiredArgsConstructor
	class TabCompleteArg {
		@NonNull
		private int index;
		@NonNull
		private String realArg;
		private String pathArg;
		@Accessors(fluent = true)
		private boolean isCompletionIndex = false;
		private Integer paramIndex;
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
			if (Strings.isNullOrEmpty(pathArg) || isVariable())
				return true;

			if (isCompletionIndex)
				if (Strings.isNullOrEmpty(realArg))
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
			else if (isVariable() && tabCompleter != null) {
				CustomCommand tabCompleteCommand = command;
				if (!(tabCompleter.getDeclaringClass().equals(command.getClass()) || Modifier.isAbstract(tabCompleter.getDeclaringClass().getModifiers())))
					tabCompleteCommand = command.getNewCommand(command.getEvent(), tabCompleter.getDeclaringClass());

				if (tabCompleter.getParameterCount() == 1)
					return (List<String>) tabCompleter.invoke(tabCompleteCommand, realArg.toLowerCase());
				else if (tabCompleter.getParameterCount() == 2)
					return (List<String>) tabCompleter.invoke(tabCompleteCommand, realArg.toLowerCase(), contextArg);
				else
					throw new BNException("Unknown converter parameters in " + tabCompleter.getName());
			}

			return new ArrayList<>();
		}

		void setTabCompleter(Class<?> clazz) {
			if (Commands.getTabCompleters().containsKey(clazz))
				this.tabCompleter = Commands.getTabCompleters().get(clazz);
		}

	}

	List<String> tabComplete(TabEvent event) {
		List<String> completions = new ArrayList<>();

		for (Method method : methods) {
			if (!event.getCommand().hasPermission(event.getSender(), method))
				continue;

			TabCompleteHelper helper = new TabCompleteHelper(method, event.getArgs());
//			BNCore.log(helper.toString());
			if (!helper.pathMatches())
				continue;

			completions.addAll(helper.tabComplete());
		}

		return completions;
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
					else if (args.size() == 0 && getLiteralWords(path).length() == 0)
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

	private String getPathString(Method method) {
		return method.getAnnotation(Path.class).value().toLowerCase();
	}

	private String getLiteralWords(String path) {
		String[] pathArgs = path.split(" ");

		String literalWords = "";
		if (path.length() > 0)
			for (String pathArg : pathArgs)
				switch (left(pathArg, 1)) {
					case "[":
					case "<":
						break;
					default:
						literalWords += pathArg + " ";
				}

		literalWords = literalWords.trim().toLowerCase();
		return literalWords;
	}

	private int getRequiredArgs(String path) {
		String[] pathArgs = path.split(" ");

		int requiredArgs = 0;
		if (path.length() > 0)
			for (String pathArg : pathArgs)
				if (!pathArg.startsWith("["))
					++requiredArgs;

		return requiredArgs;
	}

}
