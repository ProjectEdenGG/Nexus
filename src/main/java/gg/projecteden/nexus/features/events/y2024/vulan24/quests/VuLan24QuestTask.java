package gg.projecteden.nexus.features.events.y2024.vulan24.quests;

import gg.projecteden.nexus.features.quests.tasks.GatherQuestTask;
import gg.projecteden.nexus.features.quests.tasks.common.IQuestTask;
import gg.projecteden.nexus.features.quests.tasks.common.QuestTask.TaskBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

import java.util.Map;

import static gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24NPC.HANH;
import static gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24NPC.PHUONG;
import static gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24NPC.THAM;
import static gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24NPC.TRUONG;
import static gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24NPC.XUAM;
import static gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24QuestItem.INCENSE;
import static gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24QuestItem.PAPER_LANTERN_FLOATING;
import static gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24QuestReward.FISHING_QUEST;
import static gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24QuestReward.HERO_QUEST;
import static gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24QuestReward.PAPER_QUEST;
import static gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24QuestReward.POTTERY_QUEST;
import static gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24QuestReward.STONE_QUEST;

// TODO Need specific quest items (incense, lanterns, pot, etc) - model, name, etc
// TODO Quest lore is long, needs trimming/splitting into more lines
// TODO No player dialogue - fine but consider adding
// TODO Characters with accents look awful in ingame chat https://i.imgur.com/ZlV31uU.png

@Getter
@AllArgsConstructor
public enum VuLan24QuestTask implements IQuestTask {
	STONE(GatherQuestTask.builder()
		.talkTo(TRUONG)
		.dialog(dialog -> dialog
			.npc("Xin Chao traveller! I could use some help if you have the time. I need some materials to finish this house here but I just don't have the time to get it all myself. Can you help?")
			.npc("I need 128 stone, 64 tuff & 32 terracotta to be able to finish construction. You should be able to find Stone and Tuff in some of the caves nearby, and you can find Clay along the shore of the North-Eastern side of the Island. Smelting it in the furnace at the Stone Mason will give you terracotta. Bring them back to me and I'll pay you handsomely!")
		)
		.objective("Gather 128 stone, 64 tuff & 32 terracotta")
		.gather(Map.of(Material.STONE, 128, Material.TUFF, 64, Material.TERRACOTTA, 32))
		.reminder(dialog -> dialog
			.npc("TODO Reminder") // TODO
		)
		.complete(dialog -> dialog
			.npc("Cám ơn! These will be so helpful. Here's your reward!")
			.reward(STONE_QUEST)
		)
	),
	POTTERY(GatherQuestTask.builder()
		.talkTo(THAM)
		.dialog(dialog -> dialog
			.npc("Hello, we need to prepare as much pottery as possible for the festival! We've been searching for it for months now but have come up short on the final day! Can you help us?")
			.npc("Fantastic! We need 12 sherds to craft this final piece. Bring them here and we'll help you however we can!")
		)
		.objective("Gather 12 pottery sherds")
		.gather(MaterialTag.POTTERY_SHERDS, 12)
		.reminder(dialog -> dialog
			.npc("Bring 12 pottery sherds found by brushing suspicious sand back here to receive your reward") // TODO
		)
		.complete(dialog -> dialog
			.npc("Beautiful. We'll get to work right away. In the meantime, here's something we made for you!")
			.reward(POTTERY_QUEST)
		)
	),
	HERO(GatherQuestTask.builder()
		.talkTo(XUAM)
		.dialog(dialog -> dialog
			.npc("Help! Please! Bandits have ransacked my village, Vinh Thai! There's at least 10 of them and my people don't have the tools to fight them.")
			.npc("You'll help? Thank you, thank you! You'll find Vinh Thai on the other side of the  mountain, head up past the temple and you'll find it. Be careful!")
			.npc("Wait! You might need this first.")
			.give(Material.GOLDEN_APPLE, 1)
		)
		.objective("Kill 10 pillagers, a pillager captain, and a ravager, and collect their drops")
		.gather(INCENSE, 10)
		.gather(MaterialTag.POTTERY_SHERDS, 1)
		.gather(Material.STONE, 1)// TODO Ravager item?
		.reminder(dialog -> dialog
			.npc("TODO Reminder") // TODO
		)
		.complete(dialog -> dialog
			.npc("You are a hero, a dragon from the far south. May you be showered with gifts! The other survivors and I put together what we could. Please take it as a sign of our gratitude!")
			.reward(HERO_QUEST)
		)),
	PAPER(GatherQuestTask.builder()
		.talkTo(PHUONG)
		.dialog(dialog -> dialog
			.npc("You want a lantern? Sorry my friend, we're all out of supplies. I don't think the Lantern Lighting Ceremony will happen this year if we can't find any soon.")
			.npc("But, if you really want one, maybe you can help us find the materials? We need 32 paper, 32 string, and 16 coal. If you can bring them back we'll make one for you!")
		)
		.objective("Gather 32 paper, 32 string, and 16 coal")
		.gather(Map.of(Material.PAPER, 32, Material.STRING, 32, Material.COAL, 16))
		.reminder(dialog -> dialog
			.npc("As soon as you come back with the 32 paper, 32 string, and 16 coal, we’ll get you your reward in no time!") // TODO
		)
		.complete(dialog -> dialog
			.npc("Thank you so much. Here's two lanterns we made for you. Please consider placing it at the lighting ceremony at the end of the festival!") // TODO Doesnt mention tokens/mail reward
			.give(PAPER_LANTERN_FLOATING, 2) // TODO more models?
			.reward(PAPER_QUEST)
		)),
	FISH(GatherQuestTask.builder()
		.talkTo(HANH)
		.dialog(dialog -> dialog
			.npc("These fish haven't been biting all day, maybe you might have more luck than me. Bring me back a few fish and ill give you a reward or smth") // TODO
		)
		.objective("Go fishing") // TODO
		.gather(Map.of(Material.SALMON, 1)) // TODO Maybe one of each fish?
		.reminder(dialog -> dialog
			.npc("TODO Reminder") // TODO
		)
		.complete(dialog -> dialog
			.npc("Thanks") // TODO
			.reward(FISHING_QUEST)
		)),
//	BOAT_SALESMAN(InteractQuestTask.builder()
//		.talkTo(VuLan24NPC.BOAT_SALESMAN)
//		.onNPCInteract(VuLan24NPC.BOAT_SALESMAN, npcClickEvent ->
//				new BoatPickerMenu().open(npcClickEvent.getClicker())
//		)),
	;

	private final TaskBuilder<?, ?, ?> task;

	@Override
	public TaskBuilder<?, ?, ?> get() {
		return task;
	}
}
