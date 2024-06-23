package gg.projecteden.nexus.features.quests;

import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.models.eventuser.EventUserService;

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

	static void eventTokens(UUID uuid, int amount) {
		new EventUserService().edit(uuid, user -> user.giveTokens(amount));
	}

}
