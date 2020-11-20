package me.pugabyte.nexus.features.commands.staff;

import lombok.SneakyThrows;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

@Aliases("exception")
@Permission("group.staff")
public class ErrorCommand extends CustomCommand {

	public ErrorCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@SneakyThrows
	void error() {
		throw new Exception("Test exception");
	}

}
