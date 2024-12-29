package gg.projecteden.nexus.features.quests.tasks.common;

import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.models.quests.Quest;
import gg.projecteden.nexus.models.quests.QuestTaskProgress;
import gg.projecteden.nexus.models.quests.QuesterService;

import java.util.List;

public interface IQuest {

	String name();

	default String getName() {
		return StringUtils.camelCase(name());
	}

	List<IQuestTask> getTasks();

	default void assign(HasUniqueId player) {
		final List<QuestTaskProgress> tasks = getTasks().stream().map(task -> new QuestTaskProgress(player.getUniqueId(), task)).toList();
		new QuesterService().edit(player, quester -> quester.getQuests().add(new Quest(player.getUniqueId(), this, tasks)));
	}

}
