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
	modelId = 20353,
	instrument = Instrument.DIDGERIDOO,
	step = 4
)
public class DioriteBricks implements ICustomBlock {

	@Override
	public @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return get2x2Recipe(Material.POLISHED_DIORITE);
	}

	@Override
	public @Nullable RecipeBuilder<?> getUncraftRecipe() {
		return getUncraftRecipe(Material.POLISHED_DIORITE, 1);
	}
}
