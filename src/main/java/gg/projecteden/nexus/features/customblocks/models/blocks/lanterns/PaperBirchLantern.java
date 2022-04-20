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
	name = "Birch Paper Lantern",
	modelId = 20403,
	instrument = Instrument.FLUTE,
	step = 7
)
@DirectionalConfig(
	step_NS = 8,
	step_EW = 9
)
public class PaperBirchLantern implements IDirectional {
	@Override
	public @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return getPaperLanternRecipe(Material.BIRCH_PLANKS);
	}
}
