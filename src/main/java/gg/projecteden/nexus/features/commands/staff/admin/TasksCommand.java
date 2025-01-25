package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
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

	@Path("cancel <id>")
	@Description("Cancel a task")
	void cancel(int id) {
		if (!(Tasks.isRunning(id) || Tasks.isQueued(id)))
			error("Task #" + id + " is not running or queued");

		Tasks.cancel(id);
		send(PREFIX + "Task #" + id + " cancelled");
	}

	@Path("is running <id>")
	@Description("Check if a task is running")
	void isRunning(int id) {
		send(PREFIX + "Task #" + id + (Tasks.isRunning(id) ? "&a" : "&cnot ") + " running");
	}

	@Path("is queued <id>")
	@Description("Check if a task is queued")
	void isQueued(int id) {
		send(PREFIX + "Task #" + id + (Tasks.isQueued(id) ? "&a" : "&cnot ") + " queued");
	}

	@Path("list pending [page]")
	@Description("List pending tasks")
	void listPending(@Arg("1") int page) {
		final List<BukkitTask> pending = new ArrayList<>(Tasks.getPending());
		pending.sort(Comparator.comparing(BukkitTask::getTaskId).reversed());
		send(PREFIX + "Pending tasks: " + pending.size());
		new Paginator<BukkitTask>()
			.values(pending)
			.formatter((task, index) -> json(index + " &e#" + task.getTaskId() + " &7- " + task.getOwner().getName()))
			.command("/tasks list pending")
			.page(page)
			.send();
	}

	@Path("list active [page]")
	@Description("List active tasks")
	void listActive(@Arg("1") int page) {
		final List<BukkitWorker> active = new ArrayList<>(Tasks.getActive());
		active.sort(Comparator.comparing(BukkitWorker::getTaskId).reversed());
		send(PREFIX + "Active tasks: " + active.size());
		new Paginator<BukkitWorker>()
			.values(active)
			.formatter((task, index) -> json(index + " &e#" + task.getTaskId() + " &7- " + task.getOwner().getName()))
			.command("/tasks list active")
			.page(page)
			.send();
	}

}
