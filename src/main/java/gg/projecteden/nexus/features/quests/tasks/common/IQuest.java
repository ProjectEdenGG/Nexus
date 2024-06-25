package gg.projecteden.nexus.features.quests.tasks.common;

import gg.projecteden.nexus.models.quests.Quest;
import gg.projecteden.nexus.models.quests.Quest.QuestBuilder;
import org.bukkit.entity.Player;

import java.util.List;

import static gg.projecteden.api.common.utils.StringUtils.camelCase;

public interface IQuest {

	String name();

	default String getName() {
		return camelCase(name());
	}

	List<IQuestTask> getTasks();

	default QuestBuilder build(Player player) {
		return Quest.builder().tasks(getTasks()).assign(player);
	}

}
