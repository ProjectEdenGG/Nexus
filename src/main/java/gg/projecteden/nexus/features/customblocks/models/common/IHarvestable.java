package gg.projecteden.nexus.features.customblocks.models.common;

import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ToolType;
import gg.projecteden.nexus.utils.ToolType.ToolGrade;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface IHarvestable {

	/**
	 * The tool required to harvest the block (drop the items). For example, if the required tool is a
	 * diamond pickaxe, then to harvest the block, the player must use a diamond or netherite pickaxe.
	 * @see ToolType for tool order
	 * @return The minimum tool required to harvest the block
	 */
	default Material getMinimumRequiredTool() {
		return null;
	}

	default boolean requiresCorrectToolForDrops() {
		return true;
	}

	default boolean requiresSilkTouchForDrops() {
		return false;
	}

	default boolean canHarvestWith(ItemStack tool) {
		if (requiresSilkTouchForDrops()) {
			if (!tool.getItemMeta().hasEnchant(Enchant.SILK_TOUCH))
				return false;
		}

		if (!requiresCorrectToolForDrops())
			return true;

		final Material requiredTool = getMinimumRequiredTool();

		ToolType requiredToolType = ToolType.of(requiredTool);
		ToolGrade grade = ToolGrade.of(tool);

		if (grade == null || requiredToolType == null)
			return tool.getType() == requiredTool;

		List<ToolGrade> higherGrades = grade.getHigherToolGrades();
		return requiredToolType.getTools(higherGrades).contains(tool.getType());
	}

}
