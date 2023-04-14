package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Vararg;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import lombok.NonNull;

@Permission(Group.ADMIN)
public class ConsoleCommand extends CustomCommand {

	public ConsoleCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Run a command as console")
	void run(@Optional @Vararg String command) {
		runCommandAsConsole(command);
		send(PREFIX + "Ran command &c/" + command);
	}

}
