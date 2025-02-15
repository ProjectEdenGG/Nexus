package gg.projecteden.nexus.features.events.y2021.bearfair21.quests.resources.fishing;

import gg.projecteden.nexus.features.events.y2021.bearfair21.quests.resources.fishing.BearFair21FishingLoot.FishingLootCategory;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public enum BearFair21TrashLoot {
	OLD_BOOTS(Material.LEATHER),
	RUSTY_SPOON(Material.IRON_NUGGET, Material.STICK),
	BROKEN_CD(Material.IRON_NUGGET),
	LOST_BOOK(Material.PAPER, Material.LEATHER, Material.STRING),
	SOGGY_NEWSPAPER(Material.PAPER, Material.STRING),
	DRIFTWOOD(Material.STICK, Material.BONE_MEAL),
	SEAWEED(Material.GREEN_DYE, Material.BONE_MEAL),
	TREASURE(Material.GOLD_NUGGET);

	List<Material> materials;

	BearFair21TrashLoot(Material... materials) {
		this.materials = Arrays.asList(materials);
	}

	public static List<ItemStack> from(ItemStack itemStack) {
		for (BearFair21FishingLoot fishingLoot : BearFair21FishingLoot.of(FishingLootCategory.JUNK)) {
			if (ItemUtils.isFuzzyMatch(fishingLoot.getItem(), itemStack)) {
				for (BearFair21TrashLoot trashLoot : values()) {
					if (fishingLoot.name().equalsIgnoreCase(trashLoot.name())) {
						List<ItemStack> result = new ArrayList<>();
						for (int i = 0; i < itemStack.getAmount(); i++) {
							if (RandomUtils.chanceOf(5))
								result.add(TREASURE.getItemStack());

							result.add(trashLoot.getItemStack());
						}

						return result;
					}
				}
			}
		}

		return null;
	}

	private ItemStack getItemStack() {
		return new ItemBuilder(RandomUtils.randomElement(this.materials)).build();
	}
}
