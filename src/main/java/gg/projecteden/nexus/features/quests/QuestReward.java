package gg.projecteden.nexus.features.quests;

import gg.projecteden.nexus.models.quests.Quester;

import java.util.function.BiConsumer;

public interface QuestReward {

	BiConsumer<Quester, Integer> getConsumer();

	default void apply(Quester quester) {
		apply(quester, 1);
	}

	default void apply(Quester quester, int amount) {
		getConsumer().accept(quester, amount);
	}

}
