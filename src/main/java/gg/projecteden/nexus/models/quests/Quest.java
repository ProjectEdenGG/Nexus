package gg.projecteden.nexus.models.quests;

import dev.morphia.annotations.Converters;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.quests.tasks.common.IQuest;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class Quest implements PlayerOwnedObject {
	@NonNull
	private UUID uuid;
	@NonNull
	private IQuest quest;
	@NonNull
	private List<QuestTaskProgress> taskProgress;
	private int task;

	public Quester quester() {
		return Quester.of(uuid);
	}

	public QuestTaskProgress getCurrentTaskProgress() {
		return taskProgress.get(task);
	}

	public boolean hasPreviousTask() {
		return task > 0;
	}

	public QuestTaskProgress previousTask() {
		return taskProgress.get(task - 1);
	}

	public QuestTaskProgress currentTask() {
		return taskProgress.get(task);
	}

	public boolean hasNextTask() {
		return taskProgress.size() > task + 1;
	}

	public QuestTaskProgress nextTask() {
		return taskProgress.get(task + 1);
	}

	public void incrementTask() {
		++task;
	}

	public void complete() {
		incrementTask();
	}

	public boolean isComplete() {
		return task >= taskProgress.size();
	}

}
