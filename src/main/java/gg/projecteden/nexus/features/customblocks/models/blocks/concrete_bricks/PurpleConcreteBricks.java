package gg.projecteden.nexus.features.customblocks.models.blocks.concrete_bricks;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.CustomBlockTag;
import gg.projecteden.nexus.features.customblocks.models.interfaces.ICustomBlock;
import gg.projecteden.nexus.features.customblocks.models.interfaces.IDyeable;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import lombok.NonNull;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public class PurpleConcreteBricks implements ICustomBlock, IDyeable {
	@Override
	public @NonNull Instrument getNoteBlockInstrument() {
		return Instrument.CHIME;
	}

	@Override
	public int getNoteBlockStep() {
		return 9;
	}

	@Override
	public @NonNull String getName() {
		return "Purple Concrete Bricks";
	}

	@Override
	public int getCustomModelData() {
		return 20259;
	}

	@Override
	public @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return get2x2Recipe(Material.PURPLE_CONCRETE);
	}

	@Override
	public CustomBlockTag getRedyeTag(){
		return CustomBlockTag.CONCRETE_BRICKS;
	}
}
