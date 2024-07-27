package gg.projecteden.nexus.features.events.y2024.vulan24.quests;

import gg.projecteden.nexus.features.quests.QuestItem;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public enum VuLan24QuestItem implements QuestItem {
	INCENSE(new ItemBuilder(Material.STICK).name("Incense")),
	PILLAGER_DROP(CustomMaterial.GEM_TOPAZ.getNamedItemBuilder()),
	CAPTAIN_DROP(CustomMaterial.GEM_AQUAMARINE.getNamedItemBuilder()),
	RAVAGER_DROP(CustomMaterial.GEM_ONYX.getNamedItemBuilder()),
	PAPER_LANTERN_FLOATING(new ItemBuilder(CustomMaterial.VULAN_WATER_LANTERN_LARGE).name("Lighting Ceremony Lantern")),
	PAPER_LANTERN_SINGLE(new ItemBuilder(CustomMaterial.PAPER_LANTERN_SINGLE).name("&ePaper Lantern Single")),
	PAPER_LANTERN_DOUBLE(new ItemBuilder(CustomMaterial.PAPER_LANTERN_DOUBLE).name("&ePaper Lantern Double")),
	PAPER_LANTERN_TRIPLE(new ItemBuilder(CustomMaterial.PAPER_LANTERN_TRIPLE).name("&ePaper Lantern Triple")),
	;

	private final ItemBuilder itemBuilder;

	@Override
	public ItemStack get() {
		return itemBuilder.build();
	}

}
