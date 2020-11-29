package me.pugabyte.nexus.features.commands.staff;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.block.Sign;

import java.util.HashMap;
import java.util.Map;

// TODO: read cmdsigns?

@Permission("group.staff")
public class SignLinesCommand extends CustomCommand {

	static Map<String, String[]> copyLines = new HashMap<>();

	public SignLinesCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void usage() {
		send(PREFIX + "&cCorrect Usage:");
		send("&c/signlines read");
		send("&c/signlines <--copy|--paste>");
		send("&c/signlines -<#> <text> [-<#> <text> ...]");
		send("&c/signlines -<#> null [-<#> null ...]");
	}

	@Path("[arguments...]")
	void signLines(String arguments) {
		Sign sign = getTargetSignRequired();

		String uuid = player().getUniqueId().toString();
		String[] lines = sign.getLines();

		if (arg(1).equalsIgnoreCase("read")) {
			int lineNum = 1;
			for (String line : lines)
				send(lineNum++ + ": " + line);

		} else if (arg(1).equalsIgnoreCase("--copy")) {
			copyLines.put(uuid, lines);
			send(json("&eSign copied. Use &c/signlines --paste &ewhile looking at another sign").command("/signlines --paste"));

		} else if (arg(1).equalsIgnoreCase("--paste")) {
			String[] strings = copyLines.get(uuid);
			int line = 0;
			for (String string : strings)
				sign.setLine(line++, string);
		} else {
			String[] newLines = new String[4];
			int editLine = -1;

			for (String arg : args()) {
				if (arg.matches("-[1-4]")) {
					arg = arg.replace("-", "");
					editLine = Integer.parseInt(arg) - 1;

				} else {
					if (editLine == -1)
						error("No edit line specified. /signlines -<#> <text>");
					if (editLine < 0 || editLine > 3)
						error("Edit line invalid, must be between 1 and 4");

					if (newLines[editLine] != null)
						newLines[editLine] += " " + arg;
					else
						newLines[editLine] = arg;

					if (arg.equalsIgnoreCase("null"))
						newLines[editLine] = "";
				}
			}

			int line = 0;
			for (String string : newLines) {
				if (string != null)
					sign.setLine(line, StringUtils.colorize(string.trim()));
				++line;
			}
		}

		sign.update();
	}
}
