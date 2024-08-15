package gg.projecteden.nexus.features.quests;

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
}
