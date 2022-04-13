package gg.projecteden.nexus.features.customblocks.models.blocks.compacted;

import gg.projecteden.nexus.features.customblocks.models.ICustomBlock;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SugarCaneBundle implements ICustomBlock {
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
		return "Bundle of Sugar Cane";
	}

	@Override
	public int getCustomModelData() {
		return 20069;
	}

	@Override
	public boolean canPlaceSideways() {
		return true;
	}

	@Override
	public @Nullable RecipeBuilder<?> getRecipe() {
		return compacted(Material.SUGAR_CANE);
	}

	@Override
	public @NonNull Instrument getNoteBlockInstrument_NS() {
		return getNoteBlockInstrument();
	}

	@Override
	public @NonNull Instrument getNoteBlockInstrument_EW() {
		return getNoteBlockInstrument();
	}

	@Override
	public int getNoteBlockStep_NS() {
		return 11;
	}

	@Override
	public int getNoteBlockStep_EW() {
		return 12;
	}
}
