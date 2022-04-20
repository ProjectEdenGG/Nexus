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
	name = "Crimson Shroom Lantern",
	modelId = 20407,
	instrument = Instrument.FLUTE,
	step = 19
)
@DirectionalConfig(
	step_NS = 20,
	step_EW = 21
)
public class ShroomCrimsonLantern implements IDirectional {

	@Override
	public @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return getShroomLanternRecipe(Material.CRIMSON_PLANKS);
	}
}
