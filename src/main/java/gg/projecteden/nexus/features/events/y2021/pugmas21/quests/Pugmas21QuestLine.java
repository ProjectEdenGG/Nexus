package gg.projecteden.nexus.features.events.y2021.pugmas21.quests;

import gg.projecteden.nexus.models.pugmas21.Pugmas21UserService;
import gg.projecteden.nexus.models.quests.Quester;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
public enum Pugmas21QuestLine {
	ELVES(
		Pugmas21QuestTask.CRYSTAL_REPAIR_1,
		Pugmas21QuestTask.CLEAR_SKIES,
		Pugmas21QuestTask.HOLIDAY_HEIST_ELVES
	),
	PIRATES(
		Pugmas21QuestTask.CRYSTAL_REPAIR_1,
		Pugmas21QuestTask.CLEAR_SKIES,
		Pugmas21QuestTask.HOLIDAY_HEIST_PIRATES
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
