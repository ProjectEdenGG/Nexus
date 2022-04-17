package gg.projecteden.nexus.features.customblocks.models.blocks.planks;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public class CarvedAcaciaPlanks implements ICustomBlock {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.BANJO;
	}

	@Override
	public int getNoteBlockStep() {
		return 10;
	}

	@Override
	public @NonNull String getName() {
		return "Carved Acacia Planks";
	}

	@Override
	public int getCustomModelData() {
		return 20010;
	}

	@Override
	public @Nullable Pair<RecipeBuilder<?>, Integer> getRecipe() {
		return getCombineSlabsRecipe(Material.ACACIA_SLAB);
	}
}
