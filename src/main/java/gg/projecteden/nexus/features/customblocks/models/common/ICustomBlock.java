package gg.projecteden.nexus.features.customblocks.models.common;

import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.ToolType.ToolGrade;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface ICustomBlock extends IHarvestable {
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
		return new ItemBuilder(itemMaterial).modelId(getModelId()).name(getItemName());
	}

	default @NonNull ItemStack getItemStack() {
		return getItemBuilder().build();
	}

	// Misc
	default double getBlockHardness() {
		return 0.0;
	}

	default float getBlockDamage(Player player, ItemStack tool) {
		final boolean canHarvest = canHarvestWith(tool);
		float speedMultiplier = (float) getSpeedMultiplier(tool, canHarvest);

		return BlockUtils.getBlockDamage(player, tool, (float) getBlockHardness(), speedMultiplier, canHarvest, canHarvest);
	}

	default double getSpeedMultiplier(ItemStack tool, boolean canHarvest) {
		if (!canHarvest)
			return 1;

		// if not preferred -> return 1;

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
