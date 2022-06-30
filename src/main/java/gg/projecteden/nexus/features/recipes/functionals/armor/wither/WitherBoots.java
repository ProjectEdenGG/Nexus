package gg.projecteden.nexus.features.recipes.functionals.armor.wither;

import gg.projecteden.nexus.features.itemtags.Rarity;
import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shaped;

public class WitherBoots extends FunctionalRecipe {

	@Getter
	private static final ItemStack item = new ItemBuilder(CustomMaterial.WITHER_BOOTS)
		.enchant(Enchant.BLAST_PROTECTION, 4)
		.enchant(Enchant.UNBREAKING, 4)
		.name("&eWither Boots")
		.setLore(WitherHelmet.getLore())
		.rarity(Rarity.ARTIFACT)
		.nbt(nbtItem -> nbtItem.setBoolean("wither-armor", true))
		.build();

	@Override
	public ItemStack getResult() {
		return item;
	}

	@Override
	public @NotNull Recipe getRecipe() {
		return shaped("   ", "1 1", "1 1")
			.add('1', CraftedWitherSkull.getItem())
			.toMake(getResult())
			.getRecipe();
	}

	@Override
	public RecipeType getRecipeType() {
		return RecipeType.ARMOR;
	}
}
