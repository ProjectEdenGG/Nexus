package gg.projecteden.nexus.features.events.y2022.easter22.quests;

import gg.projecteden.nexus.features.quests.QuestReward;
import gg.projecteden.nexus.models.costume.CostumeUserService;
import gg.projecteden.nexus.models.trophy.TrophyHolderService;
import gg.projecteden.nexus.models.trophy.TrophyType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;
import java.util.function.BiConsumer;

@Getter
@AllArgsConstructor
public enum Easter22QuestReward implements QuestReward {
	BUNNY_EARS_COSTUME((uuid, amount) -> new CostumeUserService().edit(uuid, user -> user.getOwnedCostumes().add("exclusive/hat/bunny_ears"))),
	EASTER_BASKET_TROPHY((uuid, amount) -> new TrophyHolderService().edit(uuid, user -> user.earnAndMessage(TrophyType.EASTER_2022))),
	;

	private final BiConsumer<UUID, Integer> consumer;

}
