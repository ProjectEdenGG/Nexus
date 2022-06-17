package gg.projecteden.nexus.features.recipes.models;

import gg.projecteden.nexus.features.recipes.CustomRecipes;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.utils.StringUtils.camelCase;

@Getter
@RequiredArgsConstructor
public enum RecipeType {
	MAIN(null),
	SLABS(Material.OAK_SLAB),
	QUARTZ(Material.QUARTZ),
	BEDS_BANNERS(Material.CYAN_BED) {
		@Override
		public ItemStack getItem() {
			return new ItemBuilder(Material.CYAN_BED).name("&eBeds/Banners").build();
		}
	},
	DYES(Material.YELLOW_DYE),
	WOOL(Material.WHITE_WOOL),
	STONE_BRICK(Material.STONE_BRICKS),
	MISC(Material.BLUE_ICE),
	FUNCTIONAL(Material.CHEST, 0, true),
	DECORATION(Material.AMETHYST_SHARD, 4, true),
	ARMOR(Material.DIAMOND_CHESTPLATE, 0, true),
	FURNACE(Material.FURNACE),
	STONECUTTER(Material.STONECUTTER),
	CUSTOM_BLOCKS(Material.PAPER, 20051, true),
	;

	private final Material material;
	private final int customModelData;
	private final boolean folder;

	RecipeType(Material material) {
		this(material, 0, false);
	}

	public ItemStack getItem() {
		return new ItemBuilder(material).customModelData(customModelData).name("&e" + camelCase(this)).build();
	}

	public List<NexusRecipe> getRecipes() {
		return CustomRecipes.recipes.stream().filter(nexusRecipe -> nexusRecipe.getType() == this).collect(Collectors.toList());
	}

}
