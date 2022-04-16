package gg.projecteden.nexus.features.events.y2022.easter22.quests;

import gg.projecteden.nexus.features.quests.QuestReward;
import gg.projecteden.nexus.models.costume.CostumeUserService;
import gg.projecteden.nexus.models.quests.Quester;
import gg.projecteden.nexus.models.trophy.Trophy;
import gg.projecteden.nexus.models.trophy.TrophyHolderService;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.BiConsumer;

@Getter
@AllArgsConstructor
public enum Easter22QuestReward implements QuestReward {
	BUNNY_EARS_COSTUME((quester, amount) -> new CostumeUserService().edit(quester, user -> user.getOwnedCostumes().add("exclusive/hat/bunny_ears"))),
	EASTER_BASKET_TROPHY(((quester, amount) -> new TrophyHolderService().edit(quester, user -> user.earnAndMessage(Trophy.EASTER_2022)))),
	;

	private final BiConsumer<Quester, Integer> consumer;

}
