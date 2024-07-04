package gg.projecteden.nexus.features.events.y2024.vulan24.quests;

import gg.projecteden.nexus.features.events.y2024.vulan24.VuLan24;
import gg.projecteden.nexus.features.quests.tasks.GatherQuestTask;
import gg.projecteden.nexus.features.quests.tasks.common.IQuestTask;
import gg.projecteden.nexus.features.quests.tasks.common.QuestTask.TaskBuilder;
import gg.projecteden.nexus.models.scheduledjobs.jobs.BlockRegenJob;
import gg.projecteden.nexus.utils.MaterialTag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.BrushableBlock;
import org.bukkit.event.block.Action;

import java.util.Map;

import static gg.projecteden.api.common.utils.RandomUtils.randomElement;
import static gg.projecteden.api.common.utils.RandomUtils.randomInt;
import static gg.projecteden.api.common.utils.StringUtils.asOxfordList;
import static gg.projecteden.nexus.features.events.models.EventFishingLoot.EventDefaultFishingLoot.BLOBFISH;
import static gg.projecteden.nexus.features.events.models.EventFishingLoot.EventDefaultFishingLoot.CARP;
import static gg.projecteden.nexus.features.events.models.EventFishingLoot.EventDefaultFishingLoot.PUFFERFISH;
import static gg.projecteden.nexus.features.events.models.EventFishingLoot.EventDefaultFishingLoot.SALMON;
import static gg.projecteden.nexus.features.events.models.EventFishingLoot.EventDefaultFishingLoot.STURGEON;
import static gg.projecteden.nexus.features.events.models.EventFishingLoot.EventDefaultFishingLoot.TROPICAL_FISH;
import static gg.projecteden.nexus.features.events.models.EventFishingLoot.EventDefaultFishingLoot.WOODSKIP;
import static gg.projecteden.nexus.features.events.y2024.vulan24.VuLan24.ARCHAEOLOGY_LOOT_TABLES;
import static gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24NPC.HANH;
import static gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24NPC.PHUONG;
import static gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24NPC.THAM;
import static gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24NPC.TRUONG;
import static gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24NPC.XUAM;
import static gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24QuestItem.CAPTAIN_DROP;
import static gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24QuestItem.PAPER_LANTERN_FLOATING;
import static gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24QuestItem.PILLAGER_DROP;
import static gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24QuestItem.RAVAGER_DROP;
import static gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24QuestReward.FISHING_QUEST;
import static gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24QuestReward.HERO_QUEST;
import static gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24QuestReward.PAPER_QUEST;
import static gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24QuestReward.POTTERY_QUEST;
import static gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24QuestReward.STONE_QUEST;

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
		.onBlockInteract(MaterialTag.SUSPICIOUS_BLOCKS, Action.RIGHT_CLICK_BLOCK, (event, block) -> {
			if (block.getState() instanceof BrushableBlock brushable) {
				brushable.setLootTable(randomElement(ARCHAEOLOGY_LOOT_TABLES).getLootTable());
				brushable.update();
			}
		})
		.onBlockDropItem(MaterialTag.SUSPICIOUS_BLOCKS, (event, block) -> {
			new BlockRegenJob(block.getLocation(), block.getType()).schedule(randomInt(3 * 60, 5 * 60));
		})
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
		.objective("Kill the bandits and collect their drops")
		.gather(PILLAGER_DROP, 10)
		.gather(CAPTAIN_DROP, 1)
		.gather(RAVAGER_DROP, 1)
		.reminder(dialog -> dialog
			.npc("The bandits should be by the Lagoon near our village across the mountain, come back to me with the gems they stole from me and I’ll be sure to pay you handsomely!")
		)
		.complete(dialog -> dialog
			.npc("You are a hero, a dragon from the far south. May you be showered with gifts! The other survivors and I put together what we could. Please take this as a sign of our gratitude!")
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
			.npc("Ever since Vu Lan started these fish haven’t been catching all day, maybe you might have better luck than I do.")
			.npc("Bring a few fish back to me and I’ll be sure to pay you generously!")
		)
		.objective("Go fishing") // TODO
		.gather(
			VuLan24.get().getFishingLoot(CARP).getItem(),
			VuLan24.get().getFishingLoot(SALMON).getItem(),
			VuLan24.get().getFishingLoot(TROPICAL_FISH).getItem(),
			VuLan24.get().getFishingLoot(PUFFERFISH).getItem(),
			VuLan24.get().getFishingLoot(STURGEON).getItem(),
			VuLan24.get().getFishingLoot(WOODSKIP).getItem(),
			VuLan24.get().getFishingLoot(BLOBFISH).getItem()
		)
		.reminder((dialog, items) -> dialog
			.npc(quester -> {
				var remaining = quester.getRemainingItemNames(items);
				return "I'm still looking for " + asOxfordList(remaining, ", ") + ", can you catch " + (remaining.size() == 1 ? "it" : "them") + " for me?";
			})
		)
		.complete(dialog -> dialog
			.npc("Thank you so much! This will really help keep everyone at the festival fed!")
			.reward(FISHING_QUEST)
		)),
	;

	private final TaskBuilder<?, ?, ?> task;

	@Override
	public TaskBuilder<?, ?, ?> builder() {
		return task;
	}
}
