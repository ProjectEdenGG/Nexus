package gg.projecteden.nexus.features.customblocks.models.common;

import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface ICustomBlock {
	Material itemMaterial = Material.PAPER;

	Material getBlockMaterial();

	private CustomBlockConfig getCustomBlockConfig() {
		return getClass().getAnnotation(CustomBlockConfig.class);
	}

	// Item
	default @NonNull String getName() {
		return getCustomBlockConfig().name();
	}

	default int getModelId() {
		return getCustomBlockConfig().modelId();
	}

	default @NonNull ItemBuilder getItemBuilder() {
		return new ItemBuilder(itemMaterial).customModelData(getModelId()).name(getName());
	}

	default @NonNull ItemStack getItemStack() {
		return getItemBuilder().build().clone();
	}

	// Misc
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

	// Blockdata
	BlockData getBlockData(@NonNull BlockFace facing);

	boolean equals(@NonNull BlockData blockData, @Nullable BlockFace facing);
}
