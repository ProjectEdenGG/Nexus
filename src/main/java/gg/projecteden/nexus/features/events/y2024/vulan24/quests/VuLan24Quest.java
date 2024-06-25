package gg.projecteden.nexus.features.events.y2024.vulan24.quests;

import gg.projecteden.nexus.features.quests.tasks.common.IQuest;
import gg.projecteden.nexus.features.quests.tasks.common.IQuestTask;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum VuLan24Quest implements IQuest {
	CONSTRUCTION(VuLan24QuestTask.STONE),
	POTTERY_PROBLEMS(VuLan24QuestTask.POTTERY),
	TRAGEDY_AT_VINH_THAI_LAGOON(VuLan24QuestTask.HERO),
	PAPER_SHORTAGE(VuLan24QuestTask.PAPER),
	FISHING_FRENZY(VuLan24QuestTask.FISH),
	;

	private final List<IQuestTask> tasks;

	VuLan24Quest(IQuestTask... tasks) {
		this.tasks = Arrays.asList(tasks);
	}

}
