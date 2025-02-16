package gg.projecteden.nexus.features.recipes.models;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.features.recipes.CustomRecipes;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum RecipeType {
	FOOD(Material.COOKED_BEEF),
	FUNCTIONAL(Material.CHEST),
	BACKPACKS(ItemModelType.BACKPACK_3D_BASIC),
	FURNACE(Material.FURNACE),
	DECORATION(ItemModelType.WINDCHIMES_AMETHYST),
	ARMOR(Material.DIAMOND_CHESTPLATE),
	@Disabled // TODO Custom Blocks
	STONECUTTER(Material.STONECUTTER),
	@Disabled // TODO Custom Blocks
	CUSTOM_BLOCKS(ItemModelType.BLOCKS_CRATE_APPLE),
	QUARTZ(Material.QUARTZ),
	SLABS(Material.OAK_SLAB, false),
	BEDS_BANNERS(Material.CYAN_BED) {
		@Override
		public ItemStack getItem() {
			return new ItemBuilder(Material.CYAN_BED).name("&eBeds/Banners").build();
		}
	},
	BOATS_MINECARTS(Material.OAK_CHEST_BOAT) {
		@Override
		public ItemStack getItem() {
			return new ItemBuilder(Material.OAK_CHEST_BOAT).name("&eBoats/Minecarts").build();
		}
	},
	DYES(Material.YELLOW_DYE),
	WOOL(Material.WHITE_WOOL),
	WOOD(Material.OAK_LOG),
	COPPER(Material.COPPER_BLOCK),
	STONE_BRICK(Material.STONE_BRICKS),
	CONCRETES(Material.CYAN_CONCRETE),
	MISC(Material.BLUE_ICE),
	GLASS(Material.GLASS);

	private final Material material;
	private final String modelId;
	private final boolean folder;

	RecipeType() {
		this(null, null, true);
	}

	RecipeType(Material material) {
		this(material, true);
	}

	RecipeType(Material material, boolean folder) {
		this(material, null, folder);
	}

	RecipeType(ItemModelType itemModelType) {
		this(itemModelType.getMaterial(), itemModelType.getModel(), true);
	}

	RecipeType(ItemModelType itemModelType, boolean folder) {
		this(itemModelType.getMaterial(), itemModelType.getModel(), folder);
	}

	public ItemStack getItem() {
		return new ItemBuilder(material).model(modelId).name("&e" + StringUtils.camelCase(this)).build();
	}

	public List<NexusRecipe> getRecipes() {
		return CustomRecipes.recipes.values().stream().filter(nexusRecipe -> nexusRecipe.getType() == this).collect(Collectors.toList());
	}

	public static List<RecipeType> getEnabled() {
		return Arrays.stream(values()).filter(RecipeType::isEnabled).toList();
	}

	@SneakyThrows
	public Field getField() {
		return getClass().getField(name());
	}

	public boolean isDisabled() {
		return getField().isAnnotationPresent(Disabled.class);
	}

	public boolean isEnabled() {
		return !isDisabled();
	}

}
