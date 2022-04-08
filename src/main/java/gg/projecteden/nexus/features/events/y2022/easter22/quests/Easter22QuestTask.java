package gg.projecteden.nexus.features.events.y2022.easter22.quests;

import gg.projecteden.nexus.features.quests.tasks.GatherQuestTask;
import gg.projecteden.nexus.features.quests.tasks.common.IQuestTask;
import gg.projecteden.nexus.features.quests.tasks.common.QuestTask;
import gg.projecteden.nexus.models.costume.CostumeUserService;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static gg.projecteden.nexus.features.events.y2022.easter22.Easter22.TOTAL_EASTER_EGGS;
import static gg.projecteden.nexus.features.events.y2022.easter22.quests.Easter22NPC.EASTER_BUNNY;
import static gg.projecteden.nexus.features.events.y2022.easter22.quests.Easter22QuestItem.EASTER_EGG;
import static gg.projecteden.nexus.features.events.y2022.easter22.quests.Easter22QuestItem.PAINTBRUSH;

@Getter
@AllArgsConstructor
public enum Easter22QuestTask implements IQuestTask {
	MAIN(GatherQuestTask.builder()
		.talkTo(EASTER_BUNNY)
		.dialog(dialog -> dialog
			.npc("wheres my paintbrush dammit")
			.npc("you there. find it for me")
			.player("ok")
		)
		.reminder(dialog -> dialog
			.npc("wheres my fookin paintbrush m8")
		)
		.gather(PAINTBRUSH.get())
		.complete(dialog -> dialog
			.npc("wow ur good")
		)
		.then()
		.talkTo(EASTER_BUNNY)
		.dialog(dialog -> dialog
			.npc("can u find the eggs too")
			.player("ok")
		)
		.reminder(dialog -> dialog
			.npc("u gotta find the eggs")
		)
		.gather(EASTER_EGG.get(), TOTAL_EASTER_EGGS)
		.complete(dialog -> dialog
			.npc("good job, here's some bunny ears")
			.thenRun(task -> new CostumeUserService().edit(task.getUuid(), user -> user.getOwnedCostumes().add("exclusive/hat/bunny_ears")))
			.player("thx")
		)
		.build()
	),
	;

	private final QuestTask<?, ?> task;

	@Override
	public QuestTask<?, ?> get() {
		return task;
	}
}
