package gg.projecteden.nexus.features.events.y2022.easter22.quests;

import gg.projecteden.nexus.features.quests.tasks.GatherQuestTask;
import gg.projecteden.nexus.features.quests.tasks.common.IQuestTask;
import gg.projecteden.nexus.features.quests.tasks.common.QuestTask;
import gg.projecteden.nexus.models.costume.CostumeUserService;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static gg.projecteden.nexus.features.events.y2022.easter22.Easter22.TOTAL_EASTER_EGGS;
import static gg.projecteden.nexus.features.events.y2022.easter22.quests.Easter22Entity.EASTER_BUNNY;
import static gg.projecteden.nexus.features.events.y2022.easter22.quests.Easter22QuestItem.EASTER_EGG;
import static gg.projecteden.nexus.features.events.y2022.easter22.quests.Easter22QuestItem.PAINTBRUSH;

@Getter
@AllArgsConstructor
public enum Easter22QuestTask implements IQuestTask {
	MAIN(GatherQuestTask.builder()
		.talkTo(EASTER_BUNNY)
		.dialog(dialog -> dialog
			.npc("Well this is not good, not good at all")
			.player("Hi!")
			.npc("I mean it couldn't have just grown legs and walked away... or I suppose it could have?")
			.npc("nope that's just creepy, we are avoiding that train of thought-")
			.player("Um, excuse me?")
			.npc("It's not easy to lose, it's a paintbrush after all")
			.player("Hello? Can you not hear me or something?")
			.npc("It has to be around here somewhere, I just know it is.")
			.player("Okay then, I guess not")
			.player("Maybe if I find their paintbrush, they'll finally notice I'm trying to talk with them")
		)
		.gather(PAINTBRUSH.get())
		.reminder(dialog -> dialog
			.player("They still don't seem to notice I'm here, I should really find that paintbrush")
		)
		.complete(dialog -> dialog
			.player("Hey, is this the paintbrush you're looking for?")
			.npc("Wait where did you find that?!")
			.npc("I was looking for it everywhere, like I literally thought I would never see it agai-  wait")
			.npc("How did you know I was looking for my paintbrush... Are you psychic or something?")
			.player("It wasn't that much of a secret, you were literally mumbling about it when I came over to introduce myself.")
			.npc("Omg I'm so sorry! I didn't even know, I must've been stuck in my own head again.")
			.player("Don't worry about it")
		)
		.then()
		.talkTo(EASTER_BUNNY)
		.dialog(dialog -> dialog
			.npc("Anyways, Hi I'm Easter! Your resident Easter organizer, I'm assuming you are interested in the egg hunt?")
			.player("Definitely!")
			.npc("Awesome! I would warn you about the difficulty but it seems you have a talent when it comes to finding things...")
		)
		.gather(EASTER_EGG.get(), TOTAL_EASTER_EGGS)
		.reminder(dialog -> dialog
			.npc("I appreciate your enthusiasm but you haven't found them all yet! Come back when you've gotten all 20.")
		)
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
