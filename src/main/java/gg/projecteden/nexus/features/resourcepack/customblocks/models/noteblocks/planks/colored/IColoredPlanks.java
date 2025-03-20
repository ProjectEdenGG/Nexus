package gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.colored;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlockTag;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.planks.IPlanks;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;

public interface IColoredPlanks extends IPlanks {

	default @NotNull Material getMaterial() {
		return Material.valueOf(StringUtils.camelToSnake(getClass().getSimpleName().replace("Planks", "")).toUpperCase() + "_DYE");
	}

	@Override
	default @Nullable Pair<RecipeBuilder<?>, Integer> getCraftRecipe() {
		var items = MaterialTag.PLANKS.getValues().stream().map(ItemStack::new).collect(Collectors.toList());
		items.addAll(CustomBlockTag.COLORED_PLANKS.getValues().stream().map(customBlock -> customBlock.get().getItemStack()).toList());

		return getSurroundRecipe(getMaterial(), items);
	}

}
