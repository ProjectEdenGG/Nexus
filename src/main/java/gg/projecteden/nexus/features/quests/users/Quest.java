package gg.projecteden.nexus.features.quests.users;

import gg.projecteden.interfaces.HasUniqueId;
import gg.projecteden.nexus.features.quests.tasks.common.IQuestTask;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class Quest implements PlayerOwnedObject {
	@NonNull
	private final UUID uuid;
	@NonNull
	private List<QuestTaskProgress> tasks;
	private int task;

	public Quester quester() {
		return Quester.of(uuid);
	}

	public QuestTaskProgress getCurrentTaskProgress() {
		return tasks.get(task);
	}

	public static QuestBuilder builder() {
		return new QuestBuilder();
	}

	public boolean hasPreviousTask() {
		return task > 0;
	}

	public QuestTaskProgress previousTask() {
		return tasks.get(task - 1);
	}

	public QuestTaskProgress currentTask() {
		return tasks.get(task);
	}

	public boolean hasNextTask() {
		return tasks.size() > task + 1;
	}

	public QuestTaskProgress nextTask() {
		return tasks.get(task + 1);
	}

	public void incrementTask() {
		sendMessage("&c=== Moving to next task");
		++task;
	}

	public void complete() {
		sendMessage("&c=== Complete");
		quester().getQuests().remove(this);
	}

	public static class QuestBuilder {
		private UUID uuid;
		private final List<IQuestTask> tasks = new ArrayList<>();

		public QuestBuilder task(IQuestTask task) {
			return tasks(task);
		}

		public QuestBuilder tasks(IQuestTask... tasks) {
			return tasks(List.of(tasks));
		}

		public QuestBuilder tasks(List<? extends IQuestTask> tasks) {
			this.tasks.addAll(tasks);
			return this;
		}

		public QuestBuilder assign(HasUniqueId player) {
			return assign(player.getUniqueId());
		}

		public QuestBuilder assign(UUID uuid) {
			this.uuid = uuid;
			return this;
		}

		public void start() {
			final List<QuestTaskProgress> tasks = this.tasks.stream().map(task -> new QuestTaskProgress(uuid, task)).toList();
			Quester.of(uuid).getQuests().add(new Quest(uuid, tasks));
		}
	}

}
