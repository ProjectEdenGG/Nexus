package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.api.common.utils.Utils;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Switch;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Vararg;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import lombok.NonNull;

import java.io.File;
import java.nio.file.Paths;

@Permission(Group.ADMIN)
public class BashCommand extends CustomCommand {

	public BashCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Async
	@NoLiterals
	@Description("Execute a bash command on the system")
	void run(@Vararg String command, @Switch @Optional String path) {
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
