package gg.projecteden.nexus.features.recipes.functionals.birdhouses;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.features.recipes.models.RecipeGroup;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ModelId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shaped;
import static gg.projecteden.nexus.features.resourcepack.models.CustomMaterial.BIRDHOUSE_FOREST_HORIZONTAL;
import static gg.projecteden.nexus.utils.StringUtils.camelCase;

public abstract class Birdhouse extends FunctionalRecipe {

	private static RecipeGroup group = new RecipeGroup(1, "Birdhouses", new ItemBuilder(BIRDHOUSE_FOREST_HORIZONTAL).build());

	static {
		Nexus.registerListener(new BirdhouseListener());
	}

	@Getter
	@AllArgsConstructor
	public enum BirdhouseType {
		FOREST(Material.RED_TERRACOTTA, Material.DARK_OAK_PLANKS, Material.BIRCH_PLANKS),
		ENCHANTED(Material.BLUE_TERRACOTTA, Material.DARK_PRISMARINE, Material.CYAN_CONCRETE_POWDER),
		DEPTHS(Material.GREEN_TERRACOTTA, Material.DEEPSLATE_TILES, Material.STONE),
		;

		private final Material roof, hole, siding;

		public static BirdhouseType of(ItemStack item) {
			return of(ModelId.of(item));
		}

		public static BirdhouseType of(int modelId) {
			for (BirdhouseType type : values())
				if (type.getModels().contains(modelId))
					return type;
			return null;
		}

		public Set<Integer> getModels() {
			return new HashSet<>() {{
				final int orientations = BirdhouseOrientation.values().length;
				for (int i = 0; i < orientations; i++)
					add(ordinal() * orientations + i + getBaseModelId());
			}};
		}

		public ItemBuilder getDisplayItem() {
			return new ItemBuilder(BIRDHOUSE_FOREST_HORIZONTAL)
				.name(camelCase(this) + " Birdhouse")
				.modelId(baseModel());
		}

		public static int getBaseModelId() {
			return BIRDHOUSE_FOREST_HORIZONTAL.getModelId();
		}

		public int baseModel() {
			final int orientations = BirdhouseOrientation.values().length;
			return ordinal() * orientations + getBaseModelId();
		}

		public static Set<Integer> ids() {
			return new HashSet<>() {{
				final int total = BirdhouseType.values().length * BirdhouseOrientation.values().length;
				for (int i = 0; i < total; i++)
					add(i + getBaseModelId());
			}};
		}
	}

	protected enum BirdhouseOrientation {
		HORIZONTAL,
		VERTICAL,
		HANGING,
	}

	abstract BirdhouseType getBirdhouseType();

	@Override
	public ItemStack getResult() {
		return getBirdhouseType().getDisplayItem().build();
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
			.getRecipe();
	}

	@Override
	public RecipeType getRecipeType() {
		return RecipeType.DECORATION;
	}

	@Override
	public RecipeGroup getGroup() {
		return group;
	}
}
