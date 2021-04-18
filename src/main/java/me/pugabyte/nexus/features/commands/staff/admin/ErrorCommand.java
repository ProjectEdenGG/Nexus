package me.pugabyte.nexus.features.commands.staff.admin;

import lombok.SneakyThrows;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.NexusException;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;

@Aliases("exception")
@Permission("group.admin")
public class ErrorCommand extends CustomCommand {

	public ErrorCommand(CommandEvent event) {
		super(event);
	}

	@SneakyThrows
	@Path("throwable")
	void throwable() {
		throw new Throwable("Test exception");
	}

	@SneakyThrows
	@Path("exception")
	void exception() {
		throw new Exception("Test exception");
	}

	@SneakyThrows
	@Path("runtime")
	void runtime() {
		throw new RuntimeException("Test exception");
	}

	@SneakyThrows
	@Path("nexus")
	void nexus() {
		throw new NexusException("Test exception");
	}

	@SneakyThrows
	@Path("invalidInput")
	void invalidInput() {
		throw new InvalidInputException("Test exception");
	}

}
