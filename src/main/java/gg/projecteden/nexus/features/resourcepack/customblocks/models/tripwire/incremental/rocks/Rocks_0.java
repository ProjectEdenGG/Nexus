package gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.incremental.rocks;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.CustomTripwireConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.ICraftableTripwire;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@CustomBlockConfig(
	name = "Rocks",
	itemModel = ItemModelType.ROCKS_ROCKS_0
)
@CustomTripwireConfig(
	north_NS = true,
	east_NS = true,
	south_NS = false,
	west_NS = false,
	attached_NS = false,
	disarmed_NS = false,
	powered_NS = false
)
public class Rocks_0 implements IRocks, ICraftableTripwire {

	@Override
	public @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return getCraftRecipe(Material.COBBLESTONE, 4);
	}

	@Override
	public List<NexusRecipe> getOtherRecipes() {
		return List.of(
			RecipeBuilder.shaped("11", "11").add('1', getItemStack())
				.toMake(Material.COBBLESTONE, 1)
				.unlockedBy(getItemStack())
				.unlockedBy(Material.COBBLESTONE)
				.build()
		);
	}

	@Override
	public @Nullable RecipeBuilder<?> getUncraftRecipe() {
		return getUncraftRecipe("PEBBLES_0", 4);
	}
}
