package gg.projecteden.nexus.features.recipes.functionals.armor.wither;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.quests.itemtags.ItemTagsUtils;
import gg.projecteden.nexus.features.quests.itemtags.Rarity;
import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;

public class WitherHelmet extends FunctionalRecipe {

	static {
		Nexus.registerListener(new WitherArmorListener());
	}

	@Getter
	private static final List<String> lore = new ArrayList<>() {{
		add("&f");
		add("&eSet Bonuses:");
		add("&f");
		add("&3Wither's Lifeline");
		add("&f &7When below 25% health,");
		add("&f &7you take 75% less");
		add("&f &7damage to projectile");
		add("&f &7attacks from enemies.");
		add("&f");
		add("&3Special Attack");
		add("&f &7Fire a wither skull");
		add("&f &7projectile towards the");
		add("&f &7direction you're looking.");
		add("&f &7(Cooldown: 3s)");
		add("&f");
		add("&3Double Jump");
		add("&f &7Cooldoown: 10s");
		add("&f");
	}};

	@Getter
	private static final ItemStack item = new ItemBuilder(Material.IRON_HELMET)
				.enchant(Enchant.BLAST_PROTECTION, 4)
				.enchant(Enchant.UNBREAKING, 4)
				.name("&eWither Helmet")
				.setLore(WitherHelmet.getLore())
				.rarity(Rarity.ARTIFACT)
				.customModelData(1)
				.nbt(nbtItem -> nbtItem.setBoolean("wither-armor", true))
				.build();

	@Override
	public ItemStack getResult() {
		return WitherHelmet.getItem();
	}

	@Override
	public Recipe getRecipe() {
		NamespacedKey key = new NamespacedKey(Nexus.getInstance(), "custom_wither_set_helmet");
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
		return new String[] { "111", "1 1", "   "};
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