package gg.projecteden.nexus.features.customblocks.models.blocks.stones;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

@CustomBlockConfig(
	name = "Diorite Bricks",
	modelId = 20355,
	instrument = Instrument.DIDGERIDOO,
	step = 6
)
public class GraniteBricks implements ICustomBlock {

	@Override
	public @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return get2x2Recipe(Material.POLISHED_GRANITE);
	}

	@Override
	public @Nullable RecipeBuilder<?> getUncraftRecipe() {
		return getUncraftRecipe(Material.POLISHED_GRANITE, 1);
	}
}
