package gg.projecteden.nexus.features.events.y2022.easter22.quests;

import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public enum Easter22QuestItem {
	EASTER_EGG(new ItemBuilder(Material.PAPER).name("Easter Egg")),
	PAINTBRUSH(new ItemBuilder(Material.PAPER).name("Paintbrush")),
	;

	private final ItemBuilder itemBuilder;

	public ItemStack get() {
		return itemBuilder.build();
	}


}
