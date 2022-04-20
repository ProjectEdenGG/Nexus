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
	name = "Spruce Paper Lantern",
	modelId = 20402,
	instrument = Instrument.FLUTE,
	step = 4
)
@DirectionalConfig(
	step_NS = 5,
	step_EW = 6
)
public class PaperSpruceLantern implements IDirectional {

	@Override
	public @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return getPaperLanternRecipe(Material.SPRUCE_PLANKS);
	}
}
