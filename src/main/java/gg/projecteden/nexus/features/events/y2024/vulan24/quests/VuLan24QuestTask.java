package gg.projecteden.nexus.features.events.y2024.vulan24.quests;

import gg.projecteden.api.common.utils.RandomUtils;
import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.nexus.features.events.models.EventFishingLoot;
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
import org.bukkit.entity.Pillager;
import org.bukkit.entity.Ravager;
import org.bukkit.event.block.Action;

import java.util.Map;

@Getter
@AllArgsConstructor
public enum VuLan24QuestTask implements IQuestTask {
	STONE(GatherQuestTask.builder()
		.talkTo(VuLan24NPC.TRUONG)
		.dialog(dialog -> dialog
			.npc("Welcome traveller! I could use some help if you have the time. I need some materials to finish this house here but I just don't have the time to get it all myself. Can you help?")
			.player("Certainly. What did you need?")
			.npc("I need &e128 stone, 64 tuff and 32 terracotta &fto be able to finish construction. You should be able to find stone and tuff in some of &ethe caves nearby&f.")
			.npc("And you can find &eclay &falong the &enorth-eastern shore &fof the Island. Smelting it in the furnace at the Stone Mason will give you the terracotta.")
			.npc("I think there are some miners in the cave that you can bargain with to get a pickaxe")
			.player("Awesome - I'll get right on it!")
		)
		.objective("Gather 128 stone, 64 tuff & 32 terracotta")
		.gather(Map.of(Material.STONE, 128, Material.TUFF, 64, Material.TERRACOTTA, 32))
		.reminder(dialog -> dialog
			.npc("I still need 128 stone, 64 tuff, & 32 terracotta to finish this house")
		)
		.complete(dialog -> dialog
			.npc("These will be so helpful, here are some Event Tokens as a thank you!")
			.reward(VuLan24QuestReward.STONE_QUEST)
		)
	),
	POTTERY(GatherQuestTask.builder()
		.talkTo(VuLan24NPC.THAM)
		.dialog(dialog -> dialog
			.npc("Hello! We need to prepare as much pottery as possible for the festival! We've been searching for it for months now but have come up short on the final day! Can you help us?")
			.player("Absolutely I can. I'm the master of pottery back home. What can I do?")
			.npc("Fantastic! We need &e12 sherds &fto craft this final piece. Bring them here and we'll help you however we can!")
			.player("Right... how do I get those sherds again?")
			.npc("You will have to &ecraft a brush &fand use it on &esuspicious sand &fto get the pottery sherds.")
			.player("Of course, that's right! I'll get them to you as soon as I can.")
		)
		.objective("Gather 12 pottery sherds by brushing suspicious sand")
		.gather(MaterialTag.POTTERY_SHERDS, 12)
		.onBlockInteract(MaterialTag.SUSPICIOUS_BLOCKS, Action.RIGHT_CLICK_BLOCK, (event, block) -> {
			if (block.getState() instanceof BrushableBlock brushable) {
				brushable.setLootTable(RandomUtils.randomElement(VuLan24.ARCHAEOLOGY_LOOT_TABLES).getLootTable());
				brushable.update();
			}
		})
		.onBlockDropItem(MaterialTag.SUSPICIOUS_BLOCKS, (event, block) -> {
			new BlockRegenJob(block.getLocation(), block.getType()).schedule(RandomUtils.randomInt(3 * 60, 5 * 60));
		})
		.reminder(dialog -> dialog
			.npc("I need 12 pottery sherds for the festival, can you find them for me? You will have to brush suspicious sand to extract them")
		)
		.complete(dialog -> dialog
			.npc("Beautiful! We'll get to work right away, here are some Event Tokens for your help!")
			.reward(VuLan24QuestReward.POTTERY_QUEST)
		)
	),
	HERO(GatherQuestTask.builder()
		.talkTo(VuLan24NPC.XUAM)
		.dialog(dialog -> dialog
			.npc("Help! Please! Bandits have stolen my goods at my village Vinh Thai! There's at least 10 of them and my people don't have the tools to fight them.")
			.player("Bandits? Where? I'm a professional bandit-butt-kicker!")
			.npc("You'll help? Thank you! You'll find Vinh Thai on the other side of the mountain, head up past the temple and you'll find it. Be careful!")
			.player("Thanks, {{NPC_NAME}}. I'll do what I can!")
			.npc("Wait! You might need this first.")
			.give(Material.GOLDEN_APPLE, 1)
		)
		.objective("Kill the bandits and collect 10 topaz gems, 1 aquamarine gem, and 1 onyx gem")
		.gather(VuLan24QuestItem.PILLAGER_DROP, 10)
		.gather(VuLan24QuestItem.CAPTAIN_DROP, 1)
		.gather(VuLan24QuestItem.RAVAGER_DROP, 1)
		.onLivingEntityKilledByPlayer(Ravager.class, (event, ravager) -> event.drop(VuLan24QuestItem.RAVAGER_DROP.get()))
		.onLivingEntityKilledByPlayer(Pillager.class, (event, pillager) -> event.drop(pillager.isPatrolLeader() ? VuLan24QuestItem.CAPTAIN_DROP.get() : VuLan24QuestItem.PILLAGER_DROP.get()))
		.reminder(dialog -> dialog
			.npc("The bandits should be by the Lagoon near our village across the mountain, come back to me with the gems they stole from me and I'll be sure to pay you handsomely!")
		)
		.complete(dialog -> dialog
			.npc("You are a hero, a dragon from the far south. May you be showered with gifts! We put together what we could. Please take this as a sign of our gratitude!")
			.reward(VuLan24QuestReward.HERO_QUEST)
		)),
	PAPER(GatherQuestTask.builder()
		.talkTo(VuLan24NPC.PHUONG)
		.dialog(dialog -> dialog
			.player("Hi! I'd like to buy a paper lantern for the ceremony?")
			.npc("You want a lantern? Sorry my friend, we're all out of supplies. I don't think the Lantern Lighting Ceremony will happen this year if we can't find any soon.")
			.npc("But, if you really want one, maybe you can help us find the materials?")
			.player("Yes, please! What did you need?")
			.npc("We need 32 paper, 32 string, and 16 coal. If you can bring them back we'll be sure to make some for you!")
		)
		.objective("Gather 32 paper, 32 string, and 16 coal")
		.gather(Map.of(Material.PAPER, 32, Material.STRING, 32, Material.COAL, 16))
		.reminder(dialog -> dialog
			.npc("If you can find 32 paper, 32 string, and 16 coal, we can make your lanterns in no time!")
		)
		.complete(dialog -> dialog
			.npc("Thank you so much! Here's some lanterns we made for you, as a reward for your effort, as well as 2 lanterns for you to place at this year's lantern ceremony!")
			.give(VuLan24QuestItem.PAPER_LANTERN_FLOATING, 2)
			.reward(VuLan24QuestReward.PAPER_QUEST)
		)),
	FISH(GatherQuestTask.builder()
		.talkTo(VuLan24NPC.HANH)
		.dialog(dialog -> dialog
			.npc("Ever since Vu Lan started these fish haven't been catching all day, maybe you might have better luck than I do.")
			.player("I'm pretty good at fishing, I can give it a go if you like?")
			.npc("Bring a few fish back to me and I'll be sure to pay you generously!")
		)
		.objective("Go fishing") // TODO
		.gather(
			VuLan24.get().getFishingLoot(EventFishingLoot.EventDefaultFishingLoot.CARP).getItem(),
			VuLan24.get().getFishingLoot(EventFishingLoot.EventDefaultFishingLoot.SALMON).getItem(),
			VuLan24.get().getFishingLoot(EventFishingLoot.EventDefaultFishingLoot.TROPICAL_FISH).getItem(),
			VuLan24.get().getFishingLoot(EventFishingLoot.EventDefaultFishingLoot.PUFFERFISH).getItem(),
			VuLan24.get().getFishingLoot(EventFishingLoot.EventDefaultFishingLoot.STURGEON).getItem(),
			VuLan24.get().getFishingLoot(EventFishingLoot.EventDefaultFishingLoot.WOODSKIP).getItem(),
			VuLan24.get().getFishingLoot(EventFishingLoot.EventDefaultFishingLoot.BLOBFISH).getItem()
		)
		.reminder((dialog, items) -> dialog
			.npc(quester -> {
				var remaining = quester.getRemainingItemNames(items);
				return "I'm still looking for " + StringUtils.asOxfordList(remaining, ", ") + ", can you catch " + (remaining.size() == 1 ? "it" : "them") + " for me?";
			})
		)
		.complete(dialog -> dialog
			.npc("Thank you so much! This will really help keep everyone at the festival fed!")
			.reward(VuLan24QuestReward.FISHING_QUEST)
		)),
	;

	private final TaskBuilder<?, ?, ?> task;

	@Override
	public TaskBuilder<?, ?, ?> builder() {
		return task;
	}
}
