package gg.projecteden.nexus.features.quests;

import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.inventory.ItemStack;

public interface QuestItem {

	ItemStack get();

	default ItemStack amount(int amount) {
		return new ItemBuilder(get()).amount(4).build();
	}

}
