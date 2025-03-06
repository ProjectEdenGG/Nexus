package gg.projecteden.nexus.features.titan.clientbound;

import gg.projecteden.nexus.features.titan.models.Clientbound;
import gg.projecteden.nexus.features.titan.models.CustomCreativeItem;
import gg.projecteden.nexus.features.titan.models.PluginMessage;

public class CustomBlocks extends Clientbound {
	CustomCreativeItem[] items;

	public CustomBlocks(CustomCreativeItem... items) {
		this.items = items;
	}

	@Override
	public PluginMessage getType() {
		return PluginMessage.CUSTOM_BLOCKS;
	}

}
