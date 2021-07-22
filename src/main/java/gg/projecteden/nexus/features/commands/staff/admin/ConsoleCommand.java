package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;

@Permission("group.admin")
public class ConsoleCommand extends CustomCommand {

	public ConsoleCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[command...]")
	void run(String command) {
		runCommandAsConsole(command);
		send(PREFIX + "Ran command &c/" + command);
	}

}
