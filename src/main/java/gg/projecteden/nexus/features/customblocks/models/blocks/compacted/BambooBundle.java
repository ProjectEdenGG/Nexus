package gg.projecteden.nexus.features.customblocks.models.blocks.compacted;

import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ISidewaysBlock;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BambooBundle implements ICustomBlock, ISidewaysBlock {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.BASS_DRUM;
	}

	@Override
	public int getNoteBlockStep() {
		return 10;
	}

	@Override
	public @NotNull String getName() {
		return "Bundle of Bamboo";
	}

	@Override
	public int getCustomModelData() {
		return 20060;
	}

	@Override
	public @Nullable RecipeBuilder<?> getRecipe() {
		return compacted(Material.BAMBOO);
	}

	@Override
	public @NonNull Instrument getNoteBlockInstrument_NS() {
		return getNoteBlockInstrument();
	}

	@Override
	public int getNoteBlockStep_NS() {
		return 11;
	}

	@Override
	public @NonNull Instrument getNoteBlockInstrument_EW() {
		return getNoteBlockInstrument();
	}

	@Override
	public int getNoteBlockStep_EW() {
		return 12;
	}
}
