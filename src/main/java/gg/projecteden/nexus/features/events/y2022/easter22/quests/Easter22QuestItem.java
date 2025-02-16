package gg.projecteden.nexus.features.events.y2022.easter22.quests;

import gg.projecteden.nexus.features.quests.QuestItem;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public enum Easter22QuestItem implements QuestItem {
	EASTERS_PAINTBRUSH(new ItemBuilder(ItemModelType.EASTER22_EASTERS_PAINTBRUSH).name("&eEaster's Paintbrush")),
	PAINTBRUSH(new ItemBuilder(ItemModelType.EASTER22_PAINTBRUSH).name("&ePaintbrush")),
	PRISTINE_EGG(new ItemBuilder(ItemModelType.EASTER22_PRISTINE_EGG).name("&ePristine Egg")),
	PAINTED_EGG(new ItemBuilder(ItemModelType.EASTER22_PAINTED_EGG).name("&ePainted Egg")),
	;

	private final ItemBuilder itemBuilder;

	@Override
	public ItemStack get() {
		return itemBuilder.build();
	}

}
