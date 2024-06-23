package gg.projecteden.nexus.features.events.y2024.pugmas24.quests;

import gg.projecteden.nexus.features.quests.QuestReward;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;
import java.util.function.BiConsumer;

@Getter
@AllArgsConstructor
public enum Pugmas24QuestReward implements QuestReward {
	// TODO
	;

	private final BiConsumer<UUID, Integer> consumer;
}
