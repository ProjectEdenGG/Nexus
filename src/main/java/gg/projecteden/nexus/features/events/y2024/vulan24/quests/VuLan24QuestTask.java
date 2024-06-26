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
			.npc("Welcome traveller! I could use some help if you have the time. I need some materials to finish this house here but I just don’t have the time to get it all myself. Can you help?")
			.npc("I need 128 stone, 64 tuff & 32 terracotta to be able to finish construction. You should be able to find Stone and Tuff in some of the caves nearby,")
			.npc("And you can find Clay along the shore of the North-Eastern side of the Island. Smelting it in the furnace at the Stone Mason will give you terracotta.")
		)
		.objective("Gather 128 stone, 64 tuff & 32 terracotta")
		.gather(Map.of(Material.STONE, 128, Material.TUFF, 64, Material.TERRACOTTA, 32))
		.reminder(dialog -> dialog
			.npc("I still need 128 stone, 64 tuff, & 32 terracotta to finish this house")
		)
		.complete(dialog -> dialog
			.npc("These will be so helpful, here are some Event Tokens as a thank you!")
			.reward(STONE_QUEST)
		)
	),
	POTTERY(GatherQuestTask.builder()
		.talkTo(THAM)
		.dialog(dialog -> dialog
			.npc("Hello, we need to prepare as much pottery as possible for the festival! We’ve been searching for it for months now but have come up short on the final day! Can you help us?")
			.npc("Fantastic! We need 12 sherds to craft this final piece. Bring them here and we’ll reward you however we can!")
			.npc("You will have to craft a brush and use it on suspicious sand to get the pottery sherds.")
		)
		.objective("Gather 12 pottery sherds by brushing suspicious sand")
		.gather(MaterialTag.POTTERY_SHERDS, 12)
		.reminder(dialog -> dialog
			.npc("I need 12 pottery sherds for the festival, can you find them for me? You will have to brush suspicious sand to extract them") // TODO
		)
		.complete(dialog -> dialog
			.npc("Beautiful! We'll get to work right away, here are some Event Tokens for your help!")
			.reward(POTTERY_QUEST)
		)
	),
	HERO(GatherQuestTask.builder()
		.talkTo(XUAM)
		.dialog(dialog -> dialog
			.npc("Help! Please! Bandits have ransacked my village, Vinh Thai! There’s at least 10 of them and my people don’t have the tools to fight them.")
			.npc("You’ll help? Thank you, thank you! You’ll find Vinh Thai on the other side of the mountain, head up past the temple and you’ll find it. Be careful!")
			.npc("Wait! You might need this first.")
			.give(Material.GOLDEN_APPLE, 1)
		)
		.objective("Kill 10 pillagers, a pillager captain, and a ravager, and collect their drops")
		.gather(INCENSE, 10)
		.gather(MaterialTag.POTTERY_SHERDS, 1)
		.gather(Material.STONE, 1)// TODO Ravager item?
		.reminder(dialog -> dialog
			.npc("Come back to me once you have defeated the Bandits and I’ll be sure to pay you handsomely!") // TODO
		)
		.complete(dialog -> dialog
			.npc("You are a hero, a dragon from the far south. May you be showered with gifts! The other survivors and I put together what we could. Please take these Tokens as a sign of our gratitude!")
			.reward(HERO_QUEST)
		)),
	PAPER(GatherQuestTask.builder()
		.talkTo(PHUONG)
		.dialog(dialog -> dialog
			.npc("You want a lantern? Sorry my friend, we’re all out of supplies. I don’t think the Lantern Lighting Ceremony will happen this year if we can’t find any soon.")
			.npc("But, if you really want one, maybe you can help us find the materials?")
			.npc("We need 32 Paper, 32 String, and 16 Coal. If you can bring them back we’ll be sure to make some for you!")
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
	public TaskBuilder<?, ?, ?> builder() {
		return task;
	}
}
