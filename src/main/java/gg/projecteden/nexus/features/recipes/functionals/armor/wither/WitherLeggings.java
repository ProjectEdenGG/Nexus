package gg.projecteden.nexus.features.recipes.functionals.armor.wither;

import gg.projecteden.nexus.features.itemtags.Rarity;
import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.models.CustomArmorType;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class WitherLeggings extends FunctionalRecipe {

	@Getter
	private static final ItemStack item = new ItemBuilder(CustomMaterial.WITHER_LEGGINGS)
		.dyeColor(Color.fromRGB(CustomArmorType.WITHER.getId()))
		.itemFlags(ItemFlag.HIDE_DYE)
		.attribute(Attribute.GENERIC_ARMOR, "wither-armor-points", 6, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS)
		.attribute(Attribute.GENERIC_ARMOR_TOUGHNESS, "wither-armor-toughness", 2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS)
		.enchant(Enchant.PROTECTION, 4)
		.enchant(Enchant.UNBREAKING, 4)
		.name("&eWither Leggings")
		.setLore(WitherHelmet.getLore())
		.rarity(Rarity.ARTIFACT)
		.nbt(nbtItem -> nbtItem.setBoolean("wither-armor", true))
		.build();

	@Override
	public ItemStack getResult() {
		return WitherLeggings.getItem();
	}

	@Override
	public @NotNull Recipe getRecipe() {
		return RecipeBuilder.shaped("111", "1 1", "1 1")
			.add('1', CraftedWitherSkull.getItem())
			.toMake(getResult())
			.getRecipe();
	}

	@Override
	public RecipeType getRecipeType() {
		return RecipeType.ARMOR;
	}
}
