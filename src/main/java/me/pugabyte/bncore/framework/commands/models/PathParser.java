package me.pugabyte.bncore.framework.commands.models;

import lombok.Data;
import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.pugabyte.bncore.Utils.left;

@Data
class PathParser {
	@NonNull
	Set<Method> methods;

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
						case "[": case "{":
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
