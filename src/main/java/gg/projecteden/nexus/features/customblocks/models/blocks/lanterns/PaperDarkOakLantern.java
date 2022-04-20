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
	name = "Dark Oak Paper Lantern",
	modelId = 20406,
	instrument = Instrument.FLUTE,
	step = 16
)
@DirectionalConfig(
	step_NS = 17,
	step_EW = 18
)
public class PaperDarkOakLantern implements IDirectional {
	@Override
	public @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return getPaperLanternRecipe(Material.DARK_OAK_PLANKS);
	}
}
