package gg.projecteden.nexus.features.events.y2025.pugmas25.quests;

import gg.projecteden.nexus.features.quests.tasks.common.IQuestTask;
import gg.projecteden.nexus.features.quests.tasks.common.QuestTask.TaskBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Pugmas25QuestTask implements IQuestTask {
	;

	private final TaskBuilder<?, ?, ?> task;

	@Override
	public TaskBuilder<?, ?, ?> builder() {
		return task;
	}
}
