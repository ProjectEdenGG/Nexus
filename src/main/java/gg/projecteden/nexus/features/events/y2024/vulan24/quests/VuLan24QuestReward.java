package gg.projecteden.nexus.features.events.y2024.vulan24.quests;

import gg.projecteden.nexus.features.quests.QuestReward;
import gg.projecteden.nexus.models.mail.Mailer.Mail;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
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
		Mail.fromServer(uuid, WorldGroup.SURVIVAL, VuLan24QuestItem.POT.get()).setFromName("Vu Lan").send();
	}),
	HERO_QUEST((uuid, amount) -> {
		QuestReward.eventTokens(uuid, 150);
	}),
	PAPER_QUEST((uuid, amount) -> {
		QuestReward.eventTokens(uuid, 50);
		Mail.fromServer(uuid, WorldGroup.SURVIVAL, List.of(
			VuLan24QuestItem.PAPER_LANTERN_FLOATING.getItemBuilder().clone().amount(4).build(),
			VuLan24QuestItem.PAPER_LANTERN_SINGLE.getItemBuilder().clone().amount(4).build(),
			VuLan24QuestItem.PAPER_LANTERN_DOUBLE.getItemBuilder().clone().amount(4).build(),
			VuLan24QuestItem.PAPER_LANTERN_TRIPLE.getItemBuilder().clone().amount(4).build()
		));
	}),
	FISHING_QUEST((uuid, amount) -> {
		QuestReward.eventTokens(uuid, 100);
	})
	;

	private final BiConsumer<UUID, Integer> consumer;

}
