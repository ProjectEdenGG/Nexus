package gg.projecteden.nexus.features.customblocks.models.common;

import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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
		return getItemBuilder().build();
	}

	// Misc
	default boolean isPistonPushable() {
		return getCustomBlockConfig().isPistonPushable();
	}


}
