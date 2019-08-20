package me.pugabyte.bncore.framework.commands.models;

import lombok.Data;
import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;

import java.util.List;

import static me.pugabyte.bncore.BNCore.left;

@Data
class PathParser {
	@NonNull
	Path path;

	boolean matches(List<String> args) {
		String value = path.value().toLowerCase();

		// Zero arguments
		if (args.size() == 0) {
			return value.length() == 0;
		} else if (value.length() == 0) {
			return false;
		}

		if (args.size() > value.split(" ").length) {
			return false;
		}

		int i = 1;
		for (String pathArg : value.split(" ")) {
			try {
				argSwitch:
				switch (left(pathArg, 1)) {
					case "<":
					case "[":
					case "%":
					case "{":
						break;
					case "(":
						String[] matches = pathArg.replaceAll("[()]", "").split("\\|");
						for (String match : matches) {
							if (match.equalsIgnoreCase(args.get(i - 1))) {
								break argSwitch;
							}
						}
						return false;
					default:
						if (!pathArg.equalsIgnoreCase(args.get(i - 1))) {
							return false;
						}
				}
				++i;
			} catch (ArrayIndexOutOfBoundsException ex) {
				return false;
			}
		}
		return true;
	}

}
