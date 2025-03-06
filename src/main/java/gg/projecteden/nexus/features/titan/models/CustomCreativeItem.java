package gg.projecteden.nexus.features.titan.models;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.ICustomBlock;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CustomCreativeItem {

	String material;
	String displayName;
	String model;

	public CustomCreativeItem(CustomBlock customBlock) {
		material = ICustomBlock.itemMaterial.name().toLowerCase();
		displayName = customBlock.get().getItemName();
		model = customBlock.get().getModel();
	}

	public CustomCreativeItem(DecorationType decorationType) {
		material = decorationType.getConfig().getMaterial().name().toLowerCase();
		displayName = decorationType.getConfig().getName();
		model = decorationType.getConfig().getModel();
	}
}
