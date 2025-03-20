package gg.projecteden.nexus.features.resourcepack.customblocks.models.common;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlockTag;

/*
 	Only use this interface if the crafting recipe is different from the re-dye recipe,
 		otherwise add it into the crafting recipe, see IColoredPlanks#getCraftRecipe
 */
public interface IReDyeable extends ICustomBlock {

	CustomBlockTag getReDyeTag();
}
