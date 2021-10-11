package gg.projecteden.nexus.features.commands.staff.admin;

import com.google.common.base.Strings;
import gg.projecteden.annotations.Async;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.utils.Utils;
import lombok.NonNull;

import java.io.File;

@Permission("group.admin")
public class BashCommand extends CustomCommand {

	public BashCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<command...>")
	@Async
	void run(String command) {
		final String output = tryExecute(command);
		if (Strings.isNullOrEmpty(output))
			send(PREFIX + "Command executed successfully");
		else
			send(PREFIX + "&7" + output);
	}

	public static String tryExecute(String command) {
		return tryExecute(command, null);
	}

	public static String tryExecute(String command, File workingDirectory) {
		try {
			return Utils.bash(command, workingDirectory);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}
