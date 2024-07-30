package gg.projecteden.nexus.features.events.y2024.vulan24.quests.community;

import gg.projecteden.nexus.features.events.y2024.vulan24.VuLan24;
import gg.projecteden.nexus.utils.ItemBuilder.ModelId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.function.Predicate;

@Getter
@AllArgsConstructor
public enum VuLan24DailyQuest {
	COOKED_BEEF(Material.COOKED_BEEF),
	COOKED_PORKCHOP(Material.COOKED_PORKCHOP),
	COOKED_MUTTON(Material.COOKED_MUTTON),
	COOKED_CHICKEN(Material.COOKED_CHICKEN),
	SUGAR(Material.SUGAR),
	FISH(item -> VuLan24.get().getFishingLoot(item.getType(), ModelId.of(item)) != null),
	GOLDEN_CARROT(Material.GOLDEN_CARROT),
	HAY_BALE(Material.HAY_BLOCK),
	;

	VuLan24DailyQuest(Material material) {
		this.predicate = item -> item.getType() == material;
	}

	private final Predicate<ItemStack> predicate;

}
