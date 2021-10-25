package gg.projecteden.nexus.features.recipes.functionals.armor.wither;

import gg.projecteden.nexus.features.itemtags.Rarity;
import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shaped;

public class WitherLeggings extends FunctionalRecipe {

	@Getter
	private static final ItemStack item = new ItemBuilder(Material.IRON_LEGGINGS)
				.enchant(Enchant.PROTECTION, 4)
				.enchant(Enchant.UNBREAKING, 4)
				.name("&eWither Leggings")
				.setLore(WitherHelmet.getLore())
				.rarity(Rarity.ARTIFACT)
				.attribute(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.nameUUIDFromBytes("wither-armor-points".getBytes()), "wither-armor-points", 6, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS))
				.attribute(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(UUID.nameUUIDFromBytes("wither-armor-toughness".getBytes()), "wither-armor-toughness", 2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS))
				.customModelData(1)
				.nbt(nbtItem -> nbtItem.setBoolean("wither-armor", true))
				.build();

	@Override
	public ItemStack getResult() {
		return WitherLeggings.getItem();
	}

	@Override
	public @NotNull Recipe getRecipe() {
		return shaped("111", "1 1", "1 1")
			.add('1', CraftedWitherSkull.getItem())
			.toMake(getResult())
			.getRecipe();
	}

	@Override
	public RecipeType getRecipeType() {
		return RecipeType.ARMOR;
	}
}
