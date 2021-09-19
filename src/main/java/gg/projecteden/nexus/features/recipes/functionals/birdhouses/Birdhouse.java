package gg.projecteden.nexus.features.recipes.functionals.birdhouses;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static gg.projecteden.utils.StringUtils.camelCase;

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

		protected static BirdhouseType of(int customModelData) {
			for (BirdhouseType type : values())
				if (type.getModels().contains(customModelData))
					return type;
			return null;
		}

		protected ItemBuilder getDisplayItem() {
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
	public String[] getPattern() {
		return new String[]{"111", "232", "444"};
	}

	@Override
	public Recipe getRecipe() {
		final BirdhouseType type = getBirdhouseType();
		NamespacedKey key = new NamespacedKey(Nexus.getInstance(), "custom_birdhouse_" + type.name().toLowerCase());
		ShapedRecipe recipe = new ShapedRecipe(key, item);
		recipe.shape(getPattern());
		recipe.setIngredient('1', type.getRoof());
		recipe.setIngredient('2', type.getHole());
		recipe.setIngredient('3', Material.FEATHER);
		recipe.setIngredient('4', type.getSiding());
		return recipe;
	}

	@Override
	public List<ItemStack> getIngredients() {
		final BirdhouseType type = getBirdhouseType();
		return new ArrayList<>(List.of(
			new ItemStack(type.getRoof()),
			new ItemStack(type.getHole()),
			new ItemStack(Material.FEATHER),
			new ItemStack(type.getSiding())
		));
	}

	@Override
	public MaterialChoice getMaterialChoice() {
		return null;
	}

	@Override
	public RecipeType getRecipeType() {
		return RecipeType.DECORATION;
	}

}
