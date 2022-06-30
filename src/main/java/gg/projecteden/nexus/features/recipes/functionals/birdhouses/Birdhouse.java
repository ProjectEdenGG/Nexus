package gg.projecteden.nexus.features.recipes.functionals.birdhouses;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.CustomModelData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shaped;
import static gg.projecteden.nexus.utils.StringUtils.camelCase;

public abstract class Birdhouse extends FunctionalRecipe {

	static {
		Nexus.registerListener(new BirdhouseListener());
	}

	@Getter
	@AllArgsConstructor
	public enum BirdhouseType {
		FOREST(Material.RED_TERRACOTTA, Material.DARK_OAK_PLANKS, Material.BIRCH_PLANKS, Set.of(1, 2, 3)),
		ENCHANTED(Material.BLUE_TERRACOTTA, Material.DARK_PRISMARINE, Material.CYAN_CONCRETE_POWDER, Set.of(4, 5, 6)),
		DEPTHS(Material.GREEN_TERRACOTTA, Material.DEEPSLATE_TILES, Material.STONE, Set.of(7, 8, 9)),
		;

		private final Material roof, hole, siding;
		private final Set<Integer> models;

		public static BirdhouseType of(ItemStack item) {
			return of(CustomModelData.of(item));
		}

		public static BirdhouseType of(int customModelData) {
			for (BirdhouseType type : values())
				if (type.getModels().contains(customModelData))
					return type;
			return null;
		}

		public ItemBuilder getDisplayItem() {
			return new ItemBuilder(Material.OAK_WOOD)
				.name(camelCase(this) + " Birdhouse")
				.customModelData(baseModel());
		}

		public int baseModel() {
			return ordinal() * 3 + 1;
		}

		public static Set<Integer> ids() {
			return new HashSet<>() {{
				for (int i = 1; i <= BirdhouseType.values().length * BirdhouseOrientation.values().length ; i++)
					add(i);
			}};
		}
	}

	protected enum BirdhouseOrientation {
		HORIZONTAL,
		VERTICAL,
		HANGING,
	}

	@Getter
	public ItemStack item = getBirdhouseType().getDisplayItem().build();

	abstract BirdhouseType getBirdhouseType();

	@Override
	public String getPermission() {
		return null;
	}

	@Override
	public ItemStack getResult() {
		return item;
	}

	@Override
	public @NotNull Recipe getRecipe() {
		final BirdhouseType type = getBirdhouseType();
		return shaped("111", "232", "444")
			.add('1', type.getRoof())
			.add('2', type.getHole())
			.add('3', Material.FEATHER)
			.add('4', type.getSiding())
			.toMake(getResult())
			.id("birdhouse_" + type.name().toLowerCase())
			.getRecipe();
	}

	@Override
	public RecipeType getRecipeType() {
		return RecipeType.DECORATION;
	}

}
