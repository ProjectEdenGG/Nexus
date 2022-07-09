package gg.projecteden.nexus.features.customblocks.models.tripwire.incremental.pebbles;

import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.CustomTripwireConfig;
import gg.projecteden.nexus.features.customblocks.models.tripwire.common.ICraftableTripwire;
import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import org.bukkit.Material;

import java.util.List;

import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shaped;

@CustomBlockConfig(
	name = "Pebbles",
	modelId = 21106
)
@CustomTripwireConfig(
	north_NS = true,
	east_NS = true,
	south_NS = true,
	west_NS = false,
	attached_NS = true,
	disarmed_NS = false,
	powered_NS = false
)
public class Pebbles_0 implements IPebbles, ICraftableTripwire {

	@Override
	public List<NexusRecipe> getOtherRecipes() {
		return List.of(
			shaped("11", "11").add('1', getItemStack()).toMake(CustomBlock.ROCKS_0)
				.unlockedBy(getItemStack())
				.unlockedBy(Material.COBBLESTONE)
				.build()
		);
	}
}