package me.pugabyte.bncore.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

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
