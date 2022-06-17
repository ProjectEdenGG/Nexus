package gg.projecteden.nexus.features.customblocks.models.common;

import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

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
	default Set<Material> getApplicableTools() {
		return new HashSet<>();
	}

	default boolean requireApplicableTools() {
		return false;
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
