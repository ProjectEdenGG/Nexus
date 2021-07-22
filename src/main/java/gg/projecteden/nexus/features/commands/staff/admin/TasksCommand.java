package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NonNull;

@Permission("group.admin")
public class TasksCommand extends CustomCommand {

	public TasksCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("cancel")
	void cancel(int id) {
		if (!(Tasks.isRunning(id) || Tasks.isQueued(id)))
			error("Task #" + id + " is not running or queued");

		Tasks.cancel(id);
		send(PREFIX + "Task #" + id + " cancelled");
	}

	@Path("is running")
	void isRunning(int id) {
		send(PREFIX + "Task #" + id + (Tasks.isRunning(id) ? "&a" : "&cnot ") + " running");
	}

	@Path("is queued")
	void isQueued(int id) {
		send(PREFIX + "Task #" + id + (Tasks.isQueued(id) ? "&a" : "&cnot ") + " queued");
	}

}
