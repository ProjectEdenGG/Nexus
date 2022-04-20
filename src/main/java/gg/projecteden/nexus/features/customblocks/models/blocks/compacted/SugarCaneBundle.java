package gg.projecteden.nexus.features.customblocks.models.blocks.compacted;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IDirectional;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public class SugarCaneBundle implements IDirectional {

	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.BASS_DRUM;
	}

	@Override
	public int getNoteBlockStep() {
		return 19;
	}

	@Override
	public @NonNull String getName() {
		return "Bundle of Sugar Cane";
	}

	@Override
	public int getCustomModelData() {
		return 20063;
	}

	@Override
	public @NonNull Instrument getNoteBlockInstrument_NS() {
		return getNoteBlockInstrument();
	}

	@Override
	public int getNoteBlockStep_NS() {
		return 20;
	}

	@Override
	public @NonNull Instrument getNoteBlockInstrument_EW() {
		return getNoteBlockInstrument();
	}

	@Override
	public int getNoteBlockStep_EW() {
		return 21;
	}

	@Override
	public @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return getCompactRecipe(Material.SUGAR_CANE);
	}

	@Override
	public RecipeBuilder<?> getUncraftRecipe() {
		return getUncraftRecipe(Material.SUGAR_CANE, 9);
	}
}
