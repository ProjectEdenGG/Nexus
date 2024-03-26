package gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.misc;

import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.ICraftable;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.CustomTripwireConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.IRequireSupport;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

@CustomBlockConfig(
	name = "Lavender",
	modelId = 21120
)
@CustomTripwireConfig(
	north_NS = true,
	east_NS = false,
	south_NS = false,
	west_NS = true,
	attached_NS = false,
	disarmed_NS = false,
	powered_NS = false,
	customBreakSound = "block.azalea_leaves.break",
	customPlaceSound = "block.azalea_leaves.place",
	customStepSound = "block.azalea_leaves.step",
	customHitSound = "block.azalea_leaves.hit",
	customFallSound = "block.azalea_leaves.fall"
)
public class Lavender implements IRequireSupport, ICraftable {

	@Override
	public @Nullable RecipeBuilder<?> getUncraftRecipe() {
		return getUncraftRecipe(Material.PURPLE_DYE, 1);
	}

	@Override
	public boolean requiresCorrectToolForDrops() {
		return false;
	}
}
