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
import org.bukkit.inventory.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WitherLeggings extends FunctionalRecipe {

	@Getter
	private static final ItemStack item = new ItemBuilder(Material.IRON_LEGGINGS)
				.enchant(Enchant.PROTECTION, 4)
				.enchant(Enchant.UNBREAKING, 4)
				.name("&eWither Leggings")
				.setLore(WitherHelmet.getLore())
				.rarity(Rarity.ARTIFACT)
				.attribute(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(),"wither-armor-points", 6, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS))
				.attribute(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(UUID.randomUUID(), "wither-armor-toughness", 2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS))
				.customModelData(1)
				.nbt(nbtItem -> nbtItem.setBoolean("wither-armor", true))
				.build();

	@Override
	public ItemStack getResult() {
		return WitherLeggings.getItem();
	}

	@Override
	public Recipe getRecipe() {
		NamespacedKey key = new NamespacedKey(Nexus.getInstance(), "custom_wither_set_leggings");
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
		return new String[] { "111", "1 1", "1 1"};
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
