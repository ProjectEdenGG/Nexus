package gg.projecteden.nexus.features.recipes.functionals.armor.wither;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.itemtags.Rarity;
import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;

public class WitherBoots extends FunctionalRecipe {

	@Getter
	private static final ItemStack item = new ItemBuilder(Material.IRON_BOOTS)
				.enchant(Enchant.BLAST_PROTECTION, 4)
				.enchant(Enchant.UNBREAKING, 4)
				.name("&eWither Boots")
				.setLore(WitherHelmet.getLore())
				.customModelData(1)
				.rarity(Rarity.ARTIFACT)
				.nbt(nbtItem -> nbtItem.setBoolean("wither-armor", true))
				.build();

	@Override
	public ItemStack getResult() {
		return WitherBoots.getItem();
	}

	@Override
	public Recipe getRecipe() {
		NamespacedKey key = new NamespacedKey(Nexus.getInstance(), "custom_wither_set_boots");
		ShapedRecipe recipe = new ShapedRecipe(key, getResult());
		recipe.shape(getPattern());
		recipe.setIngredient('1', CraftedWitherSkull.getItem());
		return recipe;
	}

	@Override
	public List<ItemStack> getIngredients() {
		return new ArrayList<>() {{
			add(CraftedWitherSkull.getItem());
		}};
	}

	@Override
	public String[] getPattern() {
		return new String[] { "   ", "1 1", "1 1"};
	}

	@Override
	public RecipeChoice.MaterialChoice getMaterialChoice() {
		return null;
	}

	@Override
	public RecipeType getRecipeType() {
		return RecipeType.ARMOR;
	}
}
