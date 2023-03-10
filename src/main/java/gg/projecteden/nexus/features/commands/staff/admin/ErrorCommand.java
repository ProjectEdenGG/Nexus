package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.api.common.exceptions.EdenException;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.NexusException;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import lombok.SneakyThrows;

@Aliases("exception")
@Permission(Group.ADMIN)
public class ErrorCommand extends CustomCommand {

	public ErrorCommand(CommandEvent event) {
		super(event);
	}

	@SneakyThrows
	@Path("throwable")
	@Description("Test a Throwable")
	void throwable() {
		throw new Throwable("Test exception");
	}

	@SneakyThrows
	@Path("exception")
	@Description("Test an Exception")
	void exception() {
		throw new Exception("Test exception");
	}

	@Path("runtime")
	@Description("Test a RuntimeException")
	void runtime() {
		throw new RuntimeException("Test exception");
	}

	@Path("eden")
	@Description("Test an EdenException")
	void eden() {
		throw new EdenException("Test exception");
	}

	@Path("nexus")
	@Description("Test a NexusException")
	void nexus() {
		throw new NexusException("Test exception");
	}

	@Path("invalidInput")
	@Description("Test an InvalidInputException")
	void invalidInput() {
		throw new InvalidInputException("Test exception");
	}

	@Path("framework text")
	@Description("Test error() with text")
	void framework_text() {
		error("Test exception");
	}

	@Path("framework json")
	@Description("Test error() with json")
	void framework_json() {
		error(json("Test exception"));
	}

	@Path("framework text withColor")
	@Description("Test error() with colored text")
	void framework_text_withColor() {
		error("Test &eexception");
	}

	@Path("framework json withColor")
	@Description("Test error() with colored json")
	void framework_json_withColor() {
		error(json("Test &eexception"));
	}

}
