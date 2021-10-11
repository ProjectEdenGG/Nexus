package gg.projecteden.nexus.features.recipes.functionals.armor.wither;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.itemtags.Rarity;
import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WitherChestplate extends FunctionalRecipe {

	@Getter
	private static final ItemStack item = new ItemBuilder(Material.IRON_CHESTPLATE)
				.enchant(Enchant.PROTECTION, 4)
				.enchant(Enchant.UNBREAKING, 4)
				.name("&eWither Chestplate")
				.setLore(WitherHelmet.getLore())
				.customModelData(1)
				.rarity(Rarity.ARTIFACT)
				.attribute(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.nameUUIDFromBytes("wither-armor-points".getBytes()), "wither-armor-points", 8, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST))
				.attribute(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(UUID.nameUUIDFromBytes("wither-armor-toughness".getBytes()), "wither-armor-toughness", 2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST))
				.nbt(nbtItem -> nbtItem.setBoolean("wither-armor", true))
				.build();

	@Override
	public ItemStack getResult() {
		return WitherChestplate.getItem();
	}

	@Override
	public Recipe getRecipe() {
		NamespacedKey key = new NamespacedKey(Nexus.getInstance(), "custom_wither_set_chestplate");
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
		return new String[] { "1 1", "111", "111"};
	}

	@Override
	public RecipeChoice.MaterialChoice getMaterialChoice() {
		return null;
	}

	@Override
	public RecipeType getRecipeType() {
		return RecipeType.ARMOR;
	}

	@EventHandler
	public void onUpgradeToNetherite(PrepareResultEvent event) {
		for (ItemStack item : event.getInventory().getContents()) {
			if (ItemUtils.isFuzzyMatch(item, WitherChestplate.getItem())) {
				event.setResult(null);
				break;
			}
		}
	}

}
