package gg.projecteden.nexus.features.events.y2021.pugmas21.quests;

import gg.projecteden.nexus.features.quests.tasks.GatherQuestTask;
import gg.projecteden.nexus.features.quests.tasks.InteractQuestTask;
import gg.projecteden.nexus.features.quests.tasks.common.IQuestTask;
import gg.projecteden.nexus.features.quests.tasks.common.QuestTask;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static gg.projecteden.nexus.features.quests.QuestReward.EVENT_TOKENS;

@Getter
@AllArgsConstructor
public enum Pugmas21QuestTask implements IQuestTask {
	COSTUME_STORE_GATHER_WOOD(GatherQuestTask.builder()
		.talkTo(Pugmas21NPC.ROWAN)
		.instructions(dialog -> dialog
			.npc("hi i need help")
			.player("ok whats up")
			.npc("gimme wood")
		)
		.reminder(dialog -> dialog
			.npc("wheres my wood")
		)
		.gather(new ItemStack(Material.OAK_LOG, 32))
		.complete(dialog -> dialog
			.npc("thanks")
			.player("np")
		)
		.reward(EVENT_TOKENS, 40)
		.build()
	),
	INVESTIGATE_PENGUIN_MAFIA(InteractQuestTask.builder()
		.talkTo(Pugmas21NPC.FISH_VENDOR)
		.instructions(dialog -> dialog
			.npc("someone stole my fish")
			.player("oh no how can i help")
			.npc("they went that way")
		)
		.reminder(dialog -> dialog
			.npc("what are you still doing here")
			.npc("they went that way")
		)
		.then()
		.talkTo(Pugmas21NPC.OMALLEY)
		.instructions(dialog -> dialog
			.player("did u see anything fishy")
			.npc("ya that penguin")
		)
		.then()
		.talkTo(Pugmas21Entity.PENGUIN_1)
		.instructions(dialog -> dialog
			.player("did u steal")
			.npc("no of course not")
			.npc("dont go upstairs, nothing suspicious up there")
		)
		.then()
		.talkTo(Pugmas21Entity.PENGUIN_2)
		.instructions(dialog -> dialog
			.player("where'd this fish come from")
			.npc("aw u caught me")
			.player("stop it")
			.npc("ok")
		)
		.then()
		.talkTo(Pugmas21NPC.FISH_VENDOR)
		.instructions(dialog -> dialog
			.player("it was the penguins")
			.player("i told them to stop")
			.npc("ok thanks")
		)
		.reward(EVENT_TOKENS, 40)
		.build()
	),
	;

	private final QuestTask<?, ?> task;

	@Override
	public QuestTask<?, ?> get() {
		return task;
	}
}
