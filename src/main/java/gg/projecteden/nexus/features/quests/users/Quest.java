package gg.projecteden.nexus.features.quests.users;

import gg.projecteden.nexus.features.quests.tasks.common.ITask;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import lombok.Data;
import lombok.NonNull;
import me.lexikiq.HasUniqueId;

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

	public void incrementTask() {
		sendMessage("Moving to next task");
		++task;
	}

	public void complete() {
		sendMessage("Complete");
		quester().getQuests().remove(this);
	}

	public static class QuestBuilder {
		private UUID uuid;
		private List<ITask> tasks = new ArrayList<>();

		public QuestBuilder task(ITask task) {
			return tasks(task);
		}

		public QuestBuilder tasks(ITask... tasks) {
			return tasks(List.of(tasks));
		}

		public QuestBuilder tasks(List<ITask> tasks) {
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
