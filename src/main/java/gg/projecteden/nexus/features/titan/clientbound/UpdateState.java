package gg.projecteden.nexus.features.titan.clientbound;

import gg.projecteden.nexus.features.titan.models.Clientbound;
import gg.projecteden.nexus.features.titan.models.PluginMessage;
import lombok.Builder;

@Builder
public class UpdateState extends Clientbound {

	String mode;
	String worldGroup;
	String arena;
	String mechanic;
	Boolean vanished;
	Boolean afk;

	@Override
	public PluginMessage getType() {
		return PluginMessage.UPDATE_STATE;
	}
}
