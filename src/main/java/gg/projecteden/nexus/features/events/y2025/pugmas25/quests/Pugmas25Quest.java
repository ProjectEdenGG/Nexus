package gg.projecteden.nexus.features.events.y2025.pugmas25.quests;

import gg.projecteden.nexus.features.quests.tasks.common.IQuest;
import gg.projecteden.nexus.features.quests.tasks.common.IQuestTask;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

import static gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25QuestTask.BOARD_THE_TRAIN;
import static gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25QuestTask.CHECK_IN;
import static gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25QuestTask.ENTER_THE_CABIN;
import static gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25QuestTask.SNOWMEN;
import static gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25QuestTask.TALK_TO_TICKET_MASTER;

@Getter
public enum Pugmas25Quest implements IQuest {
	INTRO(TALK_TO_TICKET_MASTER, BOARD_THE_TRAIN, CHECK_IN, ENTER_THE_CABIN),
	DECORATE_SNOWMEN(SNOWMEN),
	;

	private final List<IQuestTask> tasks;

	Pugmas25Quest(IQuestTask... tasks) {
		this.tasks = Arrays.asList(tasks);
	}

}
