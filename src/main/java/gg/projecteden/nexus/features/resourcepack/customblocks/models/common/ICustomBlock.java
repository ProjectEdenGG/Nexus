package gg.projecteden.nexus.features.resourcepack.customblocks.models.common;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ToolType;
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

	default String getModel() {
		return getCustomBlockConfig().itemModel().getModel();
	}

	default @NonNull ItemBuilder getItemBuilder() {
		return new ItemBuilder(itemMaterial).model(getModel()).name(getItemName());
	}

	default @NonNull ItemStack getItemStack() {
		return getItemBuilder().build();
	}

	// Misc
	default double getBlockHardness() {
		return 0.0;
	}

	default float getBlockDamage(Player player, ItemStack tool) {
		final boolean isUsingCorrectTool = isUsingCorrectTool(tool, player);
		float speedMultiplier = (float) getSpeedMultiplier(tool, isUsingCorrectTool);

		return BlockUtils.getBlockDamage(player, tool, (float) getBlockHardness(), speedMultiplier, isUsingCorrectTool, isUsingCorrectTool);
	}

	default double getSpeedMultiplier(ItemStack tool, boolean isUsingCorrectTool) {
		if (!isUsingCorrectTool)
			return 1;

		if (getMinimumPreferredTool() != null)
			if (ToolType.of(tool) == ToolType.of(getMinimumPreferredTool()))
				return getBaseDiggingSpeedWithPreferredTool(tool);

		return 1;
	}

	default CustomBlock getCustomBlock() {
		return CustomBlock.of(this);
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
