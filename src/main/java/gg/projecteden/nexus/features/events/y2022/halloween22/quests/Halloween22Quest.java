package gg.projecteden.nexus.features.events.y2022.halloween22.quests;

import gg.projecteden.nexus.features.quests.tasks.common.IQuest;
import gg.projecteden.nexus.features.quests.tasks.common.IQuestTask;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum Halloween22Quest implements IQuest {
	;

	private final List<IQuestTask> tasks;

	Halloween22Quest(IQuestTask... tasks) {
		this.tasks = Arrays.asList(tasks);
	}

}
