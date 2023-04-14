package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.api.common.exceptions.EdenException;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
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
	@Description("Test a Throwable")
	void throwable() {
		throw new Throwable("Test exception");
	}

	@SneakyThrows
	@Description("Test an Exception")
	void exception() {
		throw new Exception("Test exception");
	}

	@Description("Test a RuntimeException")
	void runtime() {
		throw new RuntimeException("Test exception");
	}

	@Description("Test an EdenException")
	void eden() {
		throw new EdenException("Test exception");
	}

	@Description("Test a NexusException")
	void nexus() {
		throw new NexusException("Test exception");
	}

	@Description("Test an InvalidInputException")
	void invalidInput() {
		throw new InvalidInputException("Test exception");
	}

	@Description("Test error() with text")
	void framework_text() {
		error("Test exception");
	}

	@Description("Test error() with json")
	void framework_json() {
		error(json("Test exception"));
	}

	@Description("Test error() with colored text")
	void framework_text_withColor() {
		error("Test &eexception");
	}

	@Description("Test error() with colored json")
	void framework_json_withColor() {
		error(json("Test &eexception"));
	}

}
