package gg.projecteden.nexus.features.events.y2022.pride22.quests;

import gg.projecteden.nexus.features.quests.QuestItem;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public enum Pride22QuestItem implements QuestItem {
	;

	private final ItemBuilder itemBuilder;

	@Override
	public ItemStack get() {
		return itemBuilder.build();
	}

}
