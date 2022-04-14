package gg.projecteden.nexus.features.quests;

import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.quests.Quester;
import lombok.AllArgsConstructor;

import java.util.function.BiConsumer;

@AllArgsConstructor
public enum QuestReward {
	EVENT_TOKENS((quester, amount) -> new EventUserService().edit(quester, user -> user.giveTokens(amount))),
	;

	private final BiConsumer<Quester, Integer> consumer;

	public void apply(Quester quester) {
		apply(quester, 1);
	}

	public void apply(Quester quester, int amount) {
		consumer.accept(quester, amount);
	}
}
