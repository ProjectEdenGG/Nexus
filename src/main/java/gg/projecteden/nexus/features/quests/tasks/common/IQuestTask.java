package gg.projecteden.nexus.features.quests.tasks.common;

import gg.projecteden.nexus.features.quests.tasks.common.QuestTask.TaskBuilder;

public interface IQuestTask {

	TaskBuilder<?, ?, ?> get();

}
