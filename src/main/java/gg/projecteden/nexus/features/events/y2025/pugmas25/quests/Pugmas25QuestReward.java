package gg.projecteden.nexus.features.events.y2025.pugmas25.quests;

import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.quests.QuestReward;
import gg.projecteden.nexus.models.costume.CostumeUserService;
import gg.projecteden.nexus.models.pugmas25.Pugmas25User;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;
import java.util.function.BiConsumer;

@Getter
@AllArgsConstructor
public enum Pugmas25QuestReward implements QuestReward {
	SPARKLER(((uuid, amount) -> {
		new CostumeUserService().edit(uuid, user -> user.getOwnedCostumes().add("hand/misc/sparkler"));
		PlayerUtils.send(uuid, Pugmas25.PREFIX + "You now own the Sparkler hand costume!");
	}))
	;

	private final BiConsumer<UUID, Integer> consumer;
}
