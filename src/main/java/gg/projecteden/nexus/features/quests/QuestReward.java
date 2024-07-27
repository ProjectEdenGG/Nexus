package gg.projecteden.nexus.features.quests;

import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.function.BiConsumer;

public interface QuestReward {

	BiConsumer<UUID, Integer> getConsumer();

	default void apply(HasUniqueId quester) {
		apply(quester, 1);
	}

	default void apply(HasUniqueId quester, int amount) {
		final var eventUser = new EventUserService().get(quester);
		final var oldItemCount = eventUser.getRewardItems().size();
		getConsumer().accept(quester.getUniqueId(), amount);
		final var newItemCount = eventUser.getRewardItems().size();
		final var diff = newItemCount - oldItemCount;
		if (oldItemCount < newItemCount) {
			eventUser.sendMessage(StringUtils.getPrefix("Events") + "Claim your item reward" + (diff > 1 ? "s" : "") +
				" with &c/event rewards claim &3in the world of your choice");
		}
	}

	static void eventTokens(UUID uuid, int amount) {
		new EventUserService().edit(uuid, user -> user.giveTokens(amount));
	}

	static void item(UUID uuid, ItemStack item) {
		new EventUserService().edit(uuid, user -> user.addRewardItem(item));
	}

}
