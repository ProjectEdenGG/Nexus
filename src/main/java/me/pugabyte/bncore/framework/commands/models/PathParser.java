package me.pugabyte.bncore.framework.commands.models;

import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.Commands;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.TabEvent;
import org.objenesis.ObjenesisStd;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.pugabyte.bncore.utils.Utils.left;

@Data
class PathParser {
	@NonNull
	Set<Method> methods;

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
					arg.setTabCompleter(parameter.getAnnotation(Arg.class).tabCompleter());
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

		@ToString.Include
		boolean isLiteral() {
			if (pathArg == null) return false;
			return !pathArg.startsWith("{") && !pathArg.startsWith("[") && !pathArg.startsWith("<");
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
				else if (pathArg.toLowerCase().startsWith(realArg))
					return true;

			for (String option : tabComplete())
				if (option.equalsIgnoreCase(realArg))
					return true;

			return false;
		}

		@ToString.Include
		List<String> tabComplete() {
			if (isLiteral())
				return Arrays.asList(pathArg.replaceAll("\\(", "").replaceAll("\\)", "").split("\\|"));
			else if (isVariable() && tabCompleter != null)
				try {
					return (List<String>) tabCompleter.invoke(new ObjenesisStd().newInstance(tabCompleter.getDeclaringClass()), realArg.toLowerCase());
				} catch (Exception e) {
					BNCore.log("Error invoking tab completer");
					e.printStackTrace();
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
			String path = method.getAnnotation(Path.class).value().toLowerCase();
			String[] pathArgs = path.split(" ");

			String literalWords = "";
			if (path.length() > 0)
				for (String pathArg : pathArgs)
					switch (left(pathArg, 1)) {
						case "[": case "{": case "<":
							break;
						default:
							literalWords += pathArg + " ";
					}

			literalWords = literalWords.trim().toLowerCase();

			// Zero arguments
			if (args.size() == 0) {
				if (literalWords.length() == 0)
					return method;
				continue;
			}

			// Has arguments, no literal words
			if (literalWords.length() == 0) {
				if (fallback == null)
					fallback = method;
				continue;
			}

			// Has arguments, has literal worlds
			Matcher matcher = Pattern.compile("^" + literalWords + ".*").matcher(argsString);
			if (matcher.matches())
				fallback = method;
		}

		return fallback;
	}

}
