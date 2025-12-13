package gg.projecteden.nexus.features.titan.clientbound;

import gg.projecteden.nexus.features.titan.models.Clientbound;
import gg.projecteden.nexus.features.titan.models.PluginMessage;

public class BackbackConfig extends Clientbound {

	Entry[] entries;

	public BackbackConfig(Entry[] entries) {
		this.entries = entries;
	}

	@Override
	public PluginMessage getType() {
		return PluginMessage.BACKPACK_CONFIG;
	}

	public record Entry(String type, int rows) {}

}
