package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.api.common.utils.Utils;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;

import java.io.File;
import java.nio.file.Paths;

@HideFromWiki
@Permission(Group.ADMIN)
public class BashCommand extends CustomCommand {

	public BashCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Async
	@Path("<command...> [--path]")
	void run(String command, @Switch String path) {
		File file = path == null ? null : Paths.get(path).toFile();
		final String output = tryExecute(command, file);
		if (Nullables.isNullOrEmpty(output))
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
