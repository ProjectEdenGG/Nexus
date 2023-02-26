package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
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

@HideFromWiki
@Permission(Group.ADMIN)
public class TasksCommand extends CustomCommand {

	public TasksCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("cancel <id>")
	void cancel(int id) {
		if (!(Tasks.isRunning(id) || Tasks.isQueued(id)))
			error("Task #" + id + " is not running or queued");

		Tasks.cancel(id);
		send(PREFIX + "Task #" + id + " cancelled");
	}

	@Path("is running <id>")
	void isRunning(int id) {
		send(PREFIX + "Task #" + id + (Tasks.isRunning(id) ? "&a" : "&cnot ") + " running");
	}

	@Path("is queued <id>")
	void isQueued(int id) {
		send(PREFIX + "Task #" + id + (Tasks.isQueued(id) ? "&a" : "&cnot ") + " queued");
	}

	@Path("list pending [page]")
	void listPending(@Arg("1") int page) {
		final List<BukkitTask> pending = new ArrayList<>(Tasks.getPending());
		pending.sort(Comparator.comparing(BukkitTask::getTaskId).reversed());
		send(PREFIX + "Pending tasks: " + pending.size());
		paginate(pending, (task, index) -> json(index + " &e#" + task.getTaskId() + " &7- " + task.getOwner().getName()), "/tasks list pending", page);
	}

	@Path("list active [page]")
	void listActive(@Arg("1") int page) {
		final List<BukkitWorker> active = new ArrayList<>(Tasks.getActive());
		active.sort(Comparator.comparing(BukkitWorker::getTaskId).reversed());
		send(PREFIX + "Active tasks: " + active.size());
		paginate(active, (task, index) -> json(index + " &e#" + task.getTaskId() + " &7- " + task.getOwner().getName()), "/tasks list active", page);
	}

}
