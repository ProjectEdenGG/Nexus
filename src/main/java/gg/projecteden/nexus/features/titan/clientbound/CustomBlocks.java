package gg.projecteden.nexus.features.titan.clientbound;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.ICustomBlock;
import gg.projecteden.nexus.features.titan.models.Clientbound;
import gg.projecteden.nexus.features.titan.models.PluginMessage;
import lombok.AllArgsConstructor;

public class CustomBlocks extends Clientbound {
	CustomCreativeItem icon;
	CustomCreativeItem[] items;

	public CustomBlocks(CustomCreativeItem icon, CustomCreativeItem... items) {
		this.icon = icon;
		this.items = items;
	}

	@Override
	public PluginMessage getType() {
		return PluginMessage.CUSTOM_BLOCKS;
	}

	@AllArgsConstructor
	public static class CustomCreativeItem {
		String material;
		String displayName;
		String model;

		public CustomCreativeItem(CustomBlock customBlock) {
			material = ICustomBlock.itemMaterial.name().toLowerCase();
			displayName = customBlock.get().getItemName();
			model = customBlock.get().getModel();
		}
	}


}
