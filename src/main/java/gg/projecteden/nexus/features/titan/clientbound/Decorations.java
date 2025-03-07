package gg.projecteden.nexus.features.titan.clientbound;

import gg.projecteden.nexus.features.titan.models.Clientbound;
import gg.projecteden.nexus.features.titan.models.PluginMessage;

public class Decorations extends Clientbound {

	@Override
	public PluginMessage getType() {
		return PluginMessage.DECORATIONS;
	}


}
