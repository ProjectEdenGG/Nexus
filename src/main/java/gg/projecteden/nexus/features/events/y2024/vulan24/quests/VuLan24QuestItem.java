package gg.projecteden.nexus.features.events.y2024.vulan24.quests;

import gg.projecteden.nexus.features.quests.QuestItem;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public enum VuLan24QuestItem implements QuestItem {
	INCENSE(new ItemBuilder(Material.STICK).name("Incense")),
	LANTERN(new ItemBuilder(Material.LANTERN).name("Lantern")),
	POT(new ItemBuilder(Material.DECORATED_POT).name("Pot"))
	;

	private final ItemBuilder itemBuilder;

	@Override
	public ItemStack get() {
		return itemBuilder.build();
	}

}
