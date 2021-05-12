package me.pugabyte.nexus.features.events.y2021.bearfair21.quests.fishing;

import lombok.AllArgsConstructor;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.fishing.FishingLoot.FishingLootCategory;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.RandomUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public enum TrashLoot {
	OLD_BOOTS(Material.LEATHER),
	RUSTY_SPOON(Material.IRON_NUGGET, Material.STICK),
	BROKEN_CD(Material.IRON_NUGGET),
	LOST_BOOK(Material.PAPER, Material.LEATHER, Material.STRING),
	SOGGY_NEWSPAPER(Material.PAPER, Material.STRING),
	DRIFTWOOD(Material.STICK, Material.BONE_MEAL),
	SEAWEED(Material.GREEN_DYE, Material.BONE_MEAL),
	TREASURE(Material.GOLD_NUGGET, Material.GOLD_NUGGET, Material.LEATHER, Material.BONE_MEAL);

	List<Material> materials;

	TrashLoot(Material... materials) {
		this.materials = Arrays.asList(materials);
	}

	public static List<ItemStack> from(Player player, ItemStack itemStack) {
		for (FishingLoot fishingLoot : FishingLoot.of(FishingLootCategory.JUNK)) {
			if (ItemUtils.isFuzzyMatch(fishingLoot.getItem(player), itemStack)) {
				for (TrashLoot trashLoot : values()) {
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
