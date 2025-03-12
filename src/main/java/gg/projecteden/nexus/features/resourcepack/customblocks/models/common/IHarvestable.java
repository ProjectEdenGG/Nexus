package gg.projecteden.nexus.features.resourcepack.customblocks.models.common;

import gg.projecteden.nexus.features.recipes.RecipeUtils;
import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlocksLang;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.ToolType;
import gg.projecteden.nexus.utils.ToolType.ToolGrade;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface IHarvestable {

	/**
	 * The tool required to harvest the block (drop the items). For example, if the required tool is a
	 * diamond pickaxe, then to harvest the block, the player must use a diamond or netherite pickaxe.
	 * @see ToolType for tool order
	 * @return The minimum tool required to harvest the block
	 */
	default Material getMinimumPreferredTool() {
		return null;
	}

	default boolean requiresCorrectToolForDrops() {
		return true;
	}

	default boolean requiresSilkTouchForDrops() {
		return false;
	}

	default boolean prefersSilkTouchForDrops() {
		return false;
	}

	default double getBaseDiggingSpeedWithPreferredTool(ItemStack tool) {
		final ToolGrade toolGrade = ToolGrade.of(tool);
		if (toolGrade != null)
			return toolGrade.getBaseDiggingSpeed();

		return 1;
	}

	default boolean canHarvestWith(ItemStack tool, Player debugger) {
		if (requiresSilkTouchForDrops())
			if (!tool.containsEnchantment(Enchant.SILK_TOUCH))
				return false;

		return isUsingCorrectTool(tool, debugger);
	}

	default boolean isUsingCorrectTool(ItemStack tool, Player debugger) {
		if (!requiresCorrectToolForDrops()) {
			CustomBlocksLang.debug(debugger, "Doesn't require specific tool");
			return true;
		}

		final Material requiredTool = getMinimumPreferredTool();
		CustomBlocksLang.debug(debugger, "Min Preferred Tool: " + requiredTool);

		ToolType requiredToolType = ToolType.of(requiredTool);
		CustomBlocksLang.debug(debugger, "Required ToolType: " + requiredToolType);
		ToolGrade grade = ToolGrade.of(tool);
		CustomBlocksLang.debug(debugger, "Tool Grade: " + grade);

		if (grade == null || requiredToolType == null) {
			if (grade == null)
				CustomBlocksLang.debug(debugger, "grade == null");
			if (requiredToolType == null)
				CustomBlocksLang.debug(debugger, "requiredToolType == null");

			CustomBlocksLang.debug(debugger, "tool.getType() == requiredTool? --> " + (tool.getType() == requiredTool));
			return tool.getType() == requiredTool;
		}

		List<ToolGrade> higherGrades = grade.getEqualAndHigherToolGrades();
		CustomBlocksLang.debug(debugger, "Equal and Higher Grades: " + higherGrades);
		CustomBlocksLang.debug(debugger, "isCorrectTool? --> " + requiredToolType.getTools(higherGrades).contains(tool.getType()));

		return requiredToolType.getTools(higherGrades).contains(tool.getType());
	}

	default List<ItemStack> getNonSilkTouchDrops() {
		NexusRecipe nexusRecipe = CustomBlock.getRecipes().get(this.getClass());
		if (nexusRecipe == null) {
			return null;
		}

		Optional<List<ItemStack>> optionalRecipe = RecipeUtils.uncraft(nexusRecipe.getRecipe().getResult())
			.stream()
			.findFirst();

		if (optionalRecipe.isEmpty()) {
			return null;
		}

		List<ItemStack> recipe = optionalRecipe.get().stream().filter(Nullables::isNotNullOrAir).collect(Collectors.toList());
		Collections.shuffle(recipe);


		int dropAmount = RandomUtils.randomInt(Math.min(2, recipe.size()), recipe.size());
		List<ItemStack> drops = new ArrayList<>();
		for (int i = 0; i < dropAmount; i++) {
			ItemStack itemStack = recipe.get(i).clone();
			int amount = itemStack.getAmount();
			itemStack.setAmount(RandomUtils.randomInt(1, amount));
			drops.add(itemStack);
		}

		return drops;
	}

}
