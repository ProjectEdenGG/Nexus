package gg.projecteden.nexus.features.customblocks.models.interfaces.dyeable;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.customblocks.models.CustomBlockTag;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public interface IConcreteBricks extends IDyeable {
	@Override
	default CustomBlockTag getRedyeTag(){
		return CustomBlockTag.CONCRETE_BRICKS;
	}

	default Material getMaterial() {
		return Material.valueOf(getClass().getSimpleName().replace("ConcreteBricks", "").toUpperCase() + "_CONCRETE");
	}

	@Override
	default @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		return get2x2Recipe(getMaterial());
	}
}
