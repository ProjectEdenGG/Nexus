package gg.projecteden.nexus.features.events.y2022.halloween22.quests;

import gg.projecteden.nexus.features.quests.QuestReward;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;
import java.util.function.BiConsumer;

@Getter
@AllArgsConstructor
public enum Halloween22QuestReward implements QuestReward {
	;

	private final BiConsumer<UUID, Integer> consumer;

}
