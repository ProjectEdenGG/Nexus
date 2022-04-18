package gg.projecteden.nexus.features.customblocks.models.blocks.quilted_wool;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IDyeable;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public class MagentaQuiltedWool implements ICustomBlock, IDyeable {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.COW_BELL;
	}

	@Override
	public int getNoteBlockStep() {
		return 10;
	}

	@Override
	public @NonNull String getName() {
		return "Magenta Quilted Wool";
	}

	@Override
	public int getCustomModelData() {
		return 20310;
	}

	@Override
	public @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return get2x2Recipe(Material.MAGENTA_WOOL);
	}

	@Override
	public @Nullable RecipeBuilder<?> getUncraftRecipe() {
		return getUncraftRecipe(Material.MAGENTA_WOOL, 1);
	}
}
