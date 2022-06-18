package gg.projecteden.nexus.features.customblocks.models.common;

import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Tool;
import gg.projecteden.nexus.utils.Tool.ToolGrade;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface ICustomBlock {
	Material itemMaterial = Material.PAPER;

	Material getVanillaBlockMaterial();

	Material getVanillaItemMaterial();

	private CustomBlockConfig getCustomBlockConfig() {
		return getClass().getAnnotation(CustomBlockConfig.class);
	}

	// Item
	default @NonNull String getItemName() {
		return getCustomBlockConfig().name();
	}

	default int getModelId() {
		return getCustomBlockConfig().modelId();
	}

	default @NonNull ItemBuilder getItemBuilder() {
		return new ItemBuilder(itemMaterial).customModelData(getModelId()).name(getItemName()).clone();
	}

	default @NonNull ItemStack getItemStack() {
		return getItemBuilder().build().clone();
	}

	// Misc
	default double getBlockHardness() {
		return 0.0;
	}

	default Material getMinimumPreferredTool() {
		return Material.AIR;
	}

	default float getBlockDamage(Player player, ItemStack tool) {
		boolean isAcceptableTool = isAcceptableTool(tool);
		return BlockUtils.getBlockDamage(player, tool, (float) getBlockHardness(),
			isAcceptableTool, (float) getSpeedMultiplier(tool), isAcceptableTool);
	}

	default double getSpeedMultiplier(ItemStack tool) {
		ToolGrade grade = ToolGrade.of(tool);
		if (grade == null) {
			if (tool.getType() == Material.SHEARS)
				return 2;
			if (MaterialTag.SWORDS.isTagged(tool))
				return 1;

			return 1;
		}

		return grade.getBaseDiggingSpeed();
	}

	default boolean isAcceptableTool(ItemStack tool) {
		Material minimumTool = getMinimumPreferredTool();

		List<Material> acceptable = new ArrayList<>();
		acceptable.add(minimumTool);
		if (minimumTool == Material.AIR)
			return acceptable.contains(tool.getType());

		if (minimumTool == Material.SHEARS || MaterialTag.SWORDS.isTagged(minimumTool))
			acceptable.add(Material.AIR);

		ToolGrade grade = ToolGrade.of(tool);
		Tool minimumToolType = Tool.of(minimumTool);
		if (grade == null || minimumToolType == null)
			return acceptable.contains(tool.getType());

		List<ToolGrade> higherGrades = grade.getHigherToolGrades();
		acceptable.addAll(minimumToolType.getTools(higherGrades));

		return acceptable.contains(tool.getType());
	}

	default PistonPushAction getPistonPushedAction() {
		return getCustomBlockConfig().getPistonPushedAction();
	}

	enum PistonPushAction {
		MOVE,
		PREVENT,
		BREAK
	}

	// Sounds
	@NonNull String getBreakSound();

	@NonNull String getPlaceSound();

	@NonNull String getStepSound();

	@NonNull String getHitSound();

	@NonNull String getFallSound();

	// BlockData
	BlockData getBlockData(@NonNull BlockFace facing, @Nullable Block underneath);

	default String getStringBlockData(BlockData blockData) {
		return blockData.toString();
	}

	boolean equals(@NonNull BlockData blockData, @Nullable BlockFace facing, @NonNull Block underneath);
}
