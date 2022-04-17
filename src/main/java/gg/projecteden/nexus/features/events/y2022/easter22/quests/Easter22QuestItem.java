package gg.projecteden.nexus.features.events.y2022.easter22.quests;

import gg.projecteden.nexus.features.quests.QuestItem;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public enum Easter22QuestItem implements QuestItem {
	EASTERS_PAINTBRUSH(new ItemBuilder(Material.PAPER).customModelData(2000).name("&eEaster's Paintbrush")),
	PAINTBRUSH(new ItemBuilder(Material.PAPER).customModelData(2021).name("&ePaintbrush")),
	PRISTINE_EGG(new ItemBuilder(Material.PAPER).customModelData(2022).name("&ePristine Egg")),
	PAINTED_EGG(new ItemBuilder(Material.PAPER).customModelData(2023).name("&ePainted Egg")),
	;

	private final ItemBuilder itemBuilder;

	@Override
	public ItemStack get() {
		return itemBuilder.build();
	}

}
