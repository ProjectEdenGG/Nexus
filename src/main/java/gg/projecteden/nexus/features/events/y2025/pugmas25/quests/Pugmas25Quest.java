package gg.projecteden.nexus.features.events.y2025.pugmas25.quests;

import gg.projecteden.nexus.features.quests.tasks.common.IQuest;
import gg.projecteden.nexus.features.quests.tasks.common.IQuestTask;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum Pugmas25Quest implements IQuest {
	INTRO(Pugmas25QuestTask.INTRO),
	ADVENT(Pugmas25QuestTask.ADVENT),
	DECORATE_SNOWMEN(Pugmas25QuestTask.SNOWMEN),
	NUTCRACKERS(Pugmas25QuestTask.NUTCRACKER),
	;

	private final List<IQuestTask> tasks;

	Pugmas25Quest(IQuestTask... tasks) {
		this.tasks = Arrays.asList(tasks);
	}

}
