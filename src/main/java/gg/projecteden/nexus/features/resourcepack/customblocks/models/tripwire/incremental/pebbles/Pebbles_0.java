package gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.incremental.pebbles;

import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.CustomBlockConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.CustomTripwireConfig;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.ICraftableTripwire;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import org.bukkit.Material;

import java.util.List;

@CustomBlockConfig(
	name = "Pebbles",
	itemModel = ItemModelType.ROCKS_PEBBLES_0
)
@CustomTripwireConfig(
	north_NS = false,
	east_NS = true,
	south_NS = false,
	west_NS = true,
	attached_NS = true,
	disarmed_NS = false,
	powered_NS = false
)
public class Pebbles_0 implements IPebbles, ICraftableTripwire {

	@Override
	public List<NexusRecipe> getOtherRecipes() {
		return List.of(
			RecipeBuilder.shaped("11", "11").add('1', getItemStack()).toMake(CustomBlock.ROCKS_0)
				.unlockedBy(getItemStack())
				.unlockedBy(Material.COBBLESTONE)
				.build()
		);
	}
}
