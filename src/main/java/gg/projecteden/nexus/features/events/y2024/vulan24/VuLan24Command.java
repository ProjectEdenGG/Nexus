package gg.projecteden.nexus.features.events.y2024.vulan24;

import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.IEventCommand;
import gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24Quest;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.quests.QuestTaskProgress;
import gg.projecteden.nexus.models.quests.Quester;

@Aliases("vulan")
@Permission(Group.STAFF)
public class VuLan24Command extends IEventCommand {

	public VuLan24Command(CommandEvent event) {
		super(event);
	}

	@Override
	public EdenEvent getEdenEvent() {
		return VuLan24.get();
	}

	@Path("quest progress [player]")
	void quest_progress(@Arg(value = "self", permission = Group.STAFF) Quester quester) {
		var startedQuests = quester.getQuests();
		for (VuLan24Quest quest : VuLan24Quest.values()) {
			final boolean started = startedQuests.stream().anyMatch(startedQuest -> startedQuest.getTasks().stream().map(QuestTaskProgress::getTask).toList().equals(quest.getTasks()));
			final boolean completed = startedQuests.stream().anyMatch(startedQuest -> startedQuest.isComplete() && startedQuest.getTasks().stream().map(QuestTaskProgress::getTask).toList().equals(quest.getTasks()));
			send(quest.getName() + ": " + (completed ? "&aCompleted" : started ? "&eStarted" : "&cNot started"));
		}
	}

}
