package gg.projecteden.nexus.features.recipes.models;

import gg.projecteden.nexus.features.recipes.CustomRecipes;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
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
	MAIN,

	FUNCTIONAL(Material.CHEST, true),
	FURNACE(Material.FURNACE, true),
	DECORATION(CustomMaterial.WINDCHIMES_AMETHYST, true),
	ARMOR(Material.DIAMOND_CHESTPLATE, true),
	STONECUTTER(Material.STONECUTTER, true),
	CUSTOM_BLOCKS(CustomMaterial.BLOCKS_CRATE_APPLE, true),
	QUARTZ(Material.QUARTZ, true),
	SLABS(Material.OAK_SLAB, true),
	BEDS_BANNERS(Material.CYAN_BED, true) {
		@Override
		public ItemStack getItem() {
			return new ItemBuilder(Material.CYAN_BED).name("&eBeds/Banners").build();
		}
	},
	DYES(Material.YELLOW_DYE, true),
	WOOL(Material.WHITE_WOOL, true),
	WOOD(Material.OAK_LOG, true),
	COPPER(Material.COPPER_BLOCK, true),
	STONE_BRICK(Material.STONE_BRICKS, true),
	MISC(Material.BLUE_ICE, true),
	;

	private final Material material;
	private final int modelId;
	private final boolean folder;

	RecipeType() {
		this(null, 0, false);
	}

	RecipeType(Material material) {
		this(material, false);
	}

	RecipeType(Material material, boolean folder) {
		this(material, 0, folder);
	}

	RecipeType(CustomMaterial material) {
		this(material.getMaterial(), material.getModelId(), false);
	}

	RecipeType(CustomMaterial material, boolean folder) {
		this(material.getMaterial(), material.getModelId(), folder);
	}

	public ItemStack getItem() {
		return new ItemBuilder(material).modelId(modelId).name("&e" + camelCase(this)).build();
	}

	public List<NexusRecipe> getRecipes() {
		return CustomRecipes.recipes.stream().filter(nexusRecipe -> nexusRecipe.getType() == this).collect(Collectors.toList());
	}

}
