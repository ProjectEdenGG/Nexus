package gg.projecteden.nexus.features.customblocks.models.blocks.lanterns;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.annotations.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.annotations.DirectionalConfig;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IDirectional;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

@CustomBlockConfig(
	name = "Jungle Paper Lantern",
	modelId = 20404,
	instrument = Instrument.FLUTE,
	step = 10
)
@DirectionalConfig(
	step_NS = 11,
	step_EW = 12
)
public class PaperJungleLantern implements IDirectional {

	@Override
	public @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return getPaperLanternRecipe(Material.JUNGLE_PLANKS);
	}
}
