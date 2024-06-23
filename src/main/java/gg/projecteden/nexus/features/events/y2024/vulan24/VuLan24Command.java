package gg.projecteden.nexus.features.events.y2024.vulan24;

import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.IEventCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

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

	// im confused
//	@Path("questBoard")
//	void questBoard(){
//		var startedQuests = new QuesterService().get(player()).getQuests();
//		for (VuLan24QuestTask questTask : VuLan24QuestTask.values()) {
//			QuestBuilder questBuilder = new QuestBuilder().task(questTask);
//
//			questBuilder
//
//			new Quest(uuid(), questTask.get());
//		}
//
//		List<Quest> vuLanQuests = new ArrayList<>();
//		for (Quest quest : new QuesterService().get(player()).getQuests()) {
//			if (!(quest.getTaskProgress().getTask() instanceof VuLan24QuestTask))
//				continue;
//
//			if (quest.isComplete())
//				continue;
//
//			vuLanQuests.add(quest);
//		}
//
//		for (Quest quest : vuLanQuests) {
//			var taskProgress = quest.getTaskProgress();
//			var taskStep = taskProgress.get().getSteps().get(taskProgress.getStep());
//
//			String npcName = taskStep.getInteractable().getName();
//			String objective = new JsonBuilder(taskStep.getObjective()).toString();
//
//			send("NPC: " + npcName);
//			send("Objective: " + objective);
//			line();
//		}
//	}

}
