package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.api.common.exceptions.EdenException;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.NexusException;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import lombok.SneakyThrows;

@HideFromWiki
@Aliases("exception")
@Permission(Group.ADMIN)
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

	@Path("runtime")
	void runtime() {
		throw new RuntimeException("Test exception");
	}

	@Path("nexus")
	void nexus() {
		throw new NexusException("Test exception");
	}

	@Path("invalidInput")
	void invalidInput() {
		throw new InvalidInputException("Test exception");
	}

	@Path("framework text")
	void framework_text() {
		error("Test exception");
	}

	@Path("framework json")
	void framework_json() {
		error(json("Test exception"));
	}

	@Path("framework text withColor")
	void framework_text_withColor() {
		error("Test &eexception");
	}

	@Path("framework json withColor")
	void framework_json_withColor() {
		error(json("Test &eexception"));
	}

	@Path("api")
	void api_text() {
		throw new EdenException("Test exception");
	}

}
