package gg.projecteden.nexus.features.quests;

import me.lexikiq.HasUniqueId;

import java.util.UUID;
import java.util.function.BiConsumer;

public interface QuestReward {

	BiConsumer<UUID, Integer> getConsumer();

	default void apply(HasUniqueId quester) {
		apply(quester, 1);
	}

	default void apply(HasUniqueId quester, int amount) {
		getConsumer().accept(quester.getUniqueId(), amount);
	}

}
