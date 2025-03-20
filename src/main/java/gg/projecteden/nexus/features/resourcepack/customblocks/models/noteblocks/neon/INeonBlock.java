package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.neon;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlockTag;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.IReDyeable;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.ICraftableNoteBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.ILightableNoteBlock;
import gg.projecteden.nexus.utils.ColorType;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public interface INeonBlock extends ILightableNoteBlock, ICraftableNoteBlock, IReDyeable {

	@Override
	default double getBlockHardness() {
		return 1.5;
	}

	@Override
	default Material getMinimumPreferredTool() {
		return Material.WOODEN_PICKAXE;
	}

	ColorType getColor();

	@Override
	default CustomBlockTag getReDyeTag() {
		return CustomBlockTag.NEON_BLOCK;
	}

	@Override
	default @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return getSurroundRecipe(Material.TORCH, getColor().getConcrete());
	}
}
