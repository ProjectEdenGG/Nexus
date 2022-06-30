package gg.projecteden.nexus.features.quests;

import gg.projecteden.nexus.models.banker.Banker;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.models.voter.VoterService;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;
import java.util.function.BiConsumer;

@Getter
@AllArgsConstructor
public enum CommonQuestReward implements QuestReward {
	VOTE_POINTS((uuid, amount) -> new VoterService().edit(uuid, user -> user.givePoints(amount))),
	EVENT_TOKENS((uuid, amount) -> new EventUserService().edit(uuid, user -> user.giveTokens(amount))),
	SURVIVAL_MONEY((uuid, amount) -> new BankerService().deposit(Banker.of(uuid), amount, ShopGroup.SURVIVAL, TransactionCause.EVENT)),
	;

	private final BiConsumer<UUID, Integer> consumer;

}
