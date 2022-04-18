package gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IDyeable;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public class MagentaPlanks implements ICustomBlock, IDyeable {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.BELL;
	}

	@Override
	public int getNoteBlockStep() {
		return 10;
	}

	@Override
	public @NonNull String getName() {
		return "Magenta Planks";
	}

	@Override
	public int getCustomModelData() {
		return 20160;
	}

	@Override
	public @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return getSurroundRecipe(Material.MAGENTA_DYE, MaterialTag.PLANKS);
	}
}
