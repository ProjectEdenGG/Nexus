package gg.projecteden.nexus.features.quests.tasks.common;

import gg.projecteden.nexus.features.quests.tasks.common.QuestTask.TaskBuilder;

public interface IQuestTask {

	String name();

	default QuestTask<?, ?> get() {
		return QuestTask.CACHE.get(this);
	}

	TaskBuilder<?, ?, ?> builder();

}
