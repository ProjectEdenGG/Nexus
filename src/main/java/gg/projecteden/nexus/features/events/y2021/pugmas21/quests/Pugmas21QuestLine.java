package gg.projecteden.nexus.features.events.y2021.pugmas21.quests;

import gg.projecteden.nexus.models.pugmas21.Pugmas21UserService;
import gg.projecteden.nexus.models.quests.Quester;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.List;

import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21QuestTask.CLEAR_SKIES;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21QuestTask.CRYSTAL_REPAIR_1;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21QuestTask.HOLIDAY_HEIST_ELVES;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21QuestTask.HOLIDAY_HEIST_PIRATES;

@Getter
public enum Pugmas21QuestLine {
	ELVES(
		CRYSTAL_REPAIR_1,
		CLEAR_SKIES,
		HOLIDAY_HEIST_ELVES
	),
	PIRATES(
		CRYSTAL_REPAIR_1,
		CLEAR_SKIES,
		HOLIDAY_HEIST_PIRATES
	),
	;

	private final List<Pugmas21QuestTask> tasks;

	Pugmas21QuestLine(Pugmas21QuestTask... tasks) {
		this.tasks = List.of(tasks);
	}

	public void start(Player player) {
//		Quest.builder()
//			.tasks(tasks)
//			.assign(player)
//			.start();
	}

	public static Pugmas21QuestLine of(Quester player) {
		return new Pugmas21UserService().get(player).getQuestLine();
	}
}
