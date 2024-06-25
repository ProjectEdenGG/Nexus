package gg.projecteden.nexus.features.events.y2024.pugmas24.quests;

import gg.projecteden.nexus.features.quests.tasks.common.IQuest;
import gg.projecteden.nexus.features.quests.tasks.common.IQuestTask;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum Pugmas24Quest implements IQuest {
	;

	private final List<IQuestTask> tasks;

	Pugmas24Quest(IQuestTask... tasks) {
		this.tasks = Arrays.asList(tasks);
	}

}
