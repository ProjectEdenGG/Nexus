package gg.projecteden.nexus.models.task;

import com.dieselpoint.norm.Transaction;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.MySQLService;
import gg.projecteden.nexus.models.task.Task.Status;

import java.util.ArrayList;
import java.util.List;

public class TaskService extends MySQLService {

	public List<Task> process(String type) {
		List<Task> tasks = new ArrayList<>();
		Transaction trans = database.startTransaction();
		try {
			 tasks = database.transaction(trans)
					.where("type = ? and status = ? and timestamp <= now() for update", type, Status.PENDING.name())
					.results(Task.class);

			tasks.forEach(task -> {
				task.setStatus(Status.RUNNING);
				database.transaction(trans).upsert(task);
			});
			trans.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			trans.rollback();
		}

		return tasks;
	}

	public void complete(Task task) {
		Transaction trans = database.startTransaction();
		try {
			task.setStatus(Status.COMPLETED);
			Task first = database.transaction(trans).where("taskId = ?", task.getTaskId()).first(Task.class);
			if (first.getStatus() != Status.RUNNING)
				throw new InvalidInputException("Tried to complete a task that was not running, but was " + first.getStatus());

			database.transaction(trans).upsert(task);
			trans.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			trans.rollback();
		}
	}

}
