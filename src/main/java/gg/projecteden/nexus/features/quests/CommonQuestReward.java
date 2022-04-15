package gg.projecteden.nexus.features.quests;

import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.quests.Quester;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.BiConsumer;

@Getter
@AllArgsConstructor
public enum CommonQuestReward implements QuestReward {
	EVENT_TOKENS((quester, amount) -> new EventUserService().edit(quester, user -> user.giveTokens(amount))),
	SURVIVAL_MONEY((quester, amount) -> new BankerService().deposit(quester, amount, ShopGroup.SURVIVAL, TransactionCause.EVENT)),
	;

	private final BiConsumer<Quester, Integer> consumer;

}
