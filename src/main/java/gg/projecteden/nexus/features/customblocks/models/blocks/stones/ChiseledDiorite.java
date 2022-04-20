package gg.projecteden.nexus.features.customblocks.models.blocks.stones;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

@CustomBlockConfig(
	name = "Chiseled Diorite",
	modelId = 20354,
	instrument = Instrument.DIDGERIDOO,
	step = 5
)
public class ChiseledDiorite implements ICustomBlock {

	@Override
	public @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return getChiseledRecipe(Material.POLISHED_DIORITE_SLAB);
	}

	@Override
	public @Nullable RecipeBuilder<?> getUncraftRecipe() {
		return getUncraftRecipe(Material.POLISHED_ANDESITE_SLAB, 2);
	}
}
