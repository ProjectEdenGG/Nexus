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

	FUNCTIONAL(Material.CHEST),
	FURNACE(Material.FURNACE),
	DECORATION(CustomMaterial.WINDCHIMES_AMETHYST),
	ARMOR(Material.DIAMOND_CHESTPLATE),
	STONECUTTER(Material.STONECUTTER),
	CUSTOM_BLOCKS(CustomMaterial.BLOCKS_CRATE_APPLE),
	QUARTZ(Material.QUARTZ),
	SLABS(Material.OAK_SLAB, false),
	BEDS_BANNERS(Material.CYAN_BED) {
		@Override
		public ItemStack getItem() {
			return new ItemBuilder(Material.CYAN_BED).name("&eBeds/Banners").build();
		}
	},
	DYES(Material.YELLOW_DYE),
	WOOL(Material.WHITE_WOOL),
	WOOD(Material.OAK_LOG),
	COPPER(Material.COPPER_BLOCK),
	STONE_BRICK(Material.STONE_BRICKS),
	CONCRETES(Material.CYAN_CONCRETE),
	MISC(Material.BLUE_ICE),
	;

	private final Material material;
	private final int modelId;
	private final boolean folder;

	RecipeType() {
		this(null, 0, true);
	}

	RecipeType(Material material) {
		this(material, true);
	}

	RecipeType(Material material, boolean folder) {
		this(material, 0, folder);
	}

	RecipeType(CustomMaterial material) {
		this(material.getMaterial(), material.getModelId(), true);
	}

	RecipeType(CustomMaterial material, boolean folder) {
		this(material.getMaterial(), material.getModelId(), folder);
	}

	public ItemStack getItem() {
		return new ItemBuilder(material).modelId(modelId).name("&e" + camelCase(this)).build();
	}

	public List<NexusRecipe> getRecipes() {
		return CustomRecipes.recipes.values().stream().filter(nexusRecipe -> nexusRecipe.getType() == this).collect(Collectors.toList());
	}

}
