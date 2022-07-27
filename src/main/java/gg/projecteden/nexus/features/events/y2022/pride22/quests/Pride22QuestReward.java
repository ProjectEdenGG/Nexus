package gg.projecteden.nexus.features.events.y2022.pride22.quests;

import gg.projecteden.nexus.features.quests.QuestReward;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;
import java.util.function.BiConsumer;

@Getter
@AllArgsConstructor
public enum Pride22QuestReward implements QuestReward {
	;

	private final BiConsumer<UUID, Integer> consumer;

}
