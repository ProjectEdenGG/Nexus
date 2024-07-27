package gg.projecteden.nexus.features.events.y2024.vulan24.quests;

import gg.projecteden.nexus.features.quests.QuestReward;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;
import java.util.function.BiConsumer;

@Getter
@AllArgsConstructor
public enum VuLan24QuestReward implements QuestReward {
	STONE_QUEST((uuid, amount) -> {
		QuestReward.eventTokens(uuid, 100);
	}),
	POTTERY_QUEST((uuid, amount) -> {
		QuestReward.eventTokens(uuid, 50);
		QuestReward.item(uuid, VuLan24QuestItem.POT.get());
	}),
	HERO_QUEST((uuid, amount) -> {
		QuestReward.eventTokens(uuid, 150);
	}),
	PAPER_QUEST((uuid, amount) -> {
		QuestReward.eventTokens(uuid, 50);
		QuestReward.item(uuid, VuLan24QuestItem.PAPER_LANTERN_FLOATING.amount(4));
		QuestReward.item(uuid, VuLan24QuestItem.PAPER_LANTERN_SINGLE.amount(4));
		QuestReward.item(uuid, VuLan24QuestItem.PAPER_LANTERN_DOUBLE.amount(4));
		QuestReward.item(uuid, VuLan24QuestItem.PAPER_LANTERN_TRIPLE.amount(4));
	}),
	FISHING_QUEST((uuid, amount) -> {
		QuestReward.eventTokens(uuid, 100);
	})
	;

	private final BiConsumer<UUID, Integer> consumer;

}
