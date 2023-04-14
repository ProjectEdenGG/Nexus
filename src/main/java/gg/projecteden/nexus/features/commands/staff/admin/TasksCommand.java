package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NonNull;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Permission(Group.ADMIN)
public class TasksCommand extends CustomCommand {

	public TasksCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Description("Cancel a task")
	void cancel(int id) {
		if (!(Tasks.isRunning(id) || Tasks.isQueued(id)))
			error("Task #" + id + " is not running or queued");

		Tasks.cancel(id);
		send(PREFIX + "Task #" + id + " cancelled");
	}

	@Description("Check if a task is running")
	void is_running(int id) {
		send(PREFIX + "Task #" + id + (Tasks.isRunning(id) ? "&a" : "&cnot ") + " running");
	}

	@Description("Check if a task is queued")
	void is_queued(int id) {
		send(PREFIX + "Task #" + id + (Tasks.isQueued(id) ? "&a" : "&cnot ") + " queued");
	}

	@Description("List pending tasks")
	void list_pending(@Optional("1") int page) {
		final List<BukkitTask> pending = new ArrayList<>(Tasks.getPending());
		pending.sort(Comparator.comparing(BukkitTask::getTaskId).reversed());
		send(PREFIX + "Pending tasks: " + pending.size());
		paginate(pending, (task, index) -> json(index + " &e#" + task.getTaskId() + " &7- " + task.getOwner().getName()), "/tasks list pending", page);
	}

	@Description("List active tasks")
	void list_active(@Optional("1") int page) {
		final List<BukkitWorker> active = new ArrayList<>(Tasks.getActive());
		active.sort(Comparator.comparing(BukkitWorker::getTaskId).reversed());
		send(PREFIX + "Active tasks: " + active.size());
		paginate(active, (task, index) -> json(index + " &e#" + task.getTaskId() + " &7- " + task.getOwner().getName()), "/tasks list active", page);
	}

}
