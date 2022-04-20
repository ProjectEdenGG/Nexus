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
	name = "Oak Paper Lantern",
	modelId = 20401,
	instrument = Instrument.FLUTE,
	step = 1
)
@DirectionalConfig(
	step_NS = 2,
	step_EW = 3
)
public class PaperOakLantern implements IDirectional {

	@Override
	public @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return getPaperLanternRecipe(Material.OAK_PLANKS);
	}
}
