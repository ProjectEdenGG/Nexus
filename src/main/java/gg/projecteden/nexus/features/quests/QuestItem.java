package gg.projecteden.nexus.features.quests;

import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.models.quests.Quester;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import org.bukkit.inventory.ItemStack;

public interface QuestItem {

	ItemBuilder getItemBuilder();

	ItemStack get();

	default ItemStack amount(int amount) {
		return new ItemBuilder(get()).amount(amount).build();
	}

	default boolean fuzzyMatch(ItemStack item) {
		return ItemUtils.isFuzzyMatch(get(), item);
	}

	default boolean isInInventoryOf(HasUniqueId uuid) {
		return Quester.of(uuid).has(get());
	}

}
