package gg.projecteden.nexus.features.titan.clientbound;

import gg.projecteden.nexus.features.titan.models.Clientbound;
import gg.projecteden.nexus.features.titan.models.CustomCreativeItem;
import gg.projecteden.nexus.features.titan.models.PluginMessage;

public class Decorations extends Clientbound {
	CustomCreativeItem[] items;

	public Decorations(CustomCreativeItem... items) {
		this.items = items;
	}

	@Override
	public PluginMessage getType() {
		return PluginMessage.DECORATIONS;
	}


}
