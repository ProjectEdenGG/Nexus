package gg.projecteden.nexus.features.events.y2022.easter22.quests;

import gg.projecteden.nexus.features.quests.tasks.common.IQuest;
import gg.projecteden.nexus.features.quests.tasks.common.IQuestTask;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum Easter22Quest implements IQuest {
	MAIN(Easter22QuestTask.MAIN);

	private final List<IQuestTask> tasks;

	Easter22Quest(IQuestTask... tasks) {
		this.tasks = Arrays.asList(tasks);
	}

}
