package gg.projecteden.nexus.features.events.y2025.pugmas25.quests;

import gg.projecteden.nexus.features.quests.QuestReward;
import gg.projecteden.nexus.models.pugmas25.Pugmas25User;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;
import java.util.function.BiConsumer;

@Getter
@AllArgsConstructor
public enum Pugmas25QuestReward implements QuestReward {
	SNOWMEN(((uuid, integer) -> new Pugmas25UserService().get(uuid).sendMessage("TODO: REWARD")))
	;

	private final BiConsumer<UUID, Integer> consumer;
}
