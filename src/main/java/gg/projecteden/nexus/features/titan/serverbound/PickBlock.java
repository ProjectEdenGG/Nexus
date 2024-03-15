package gg.projecteden.nexus.features.titan.serverbound;

import gg.projecteden.nexus.features.listeners.events.CreativePickBlockEvent;
import gg.projecteden.nexus.features.titan.models.Serverbound;
import org.bukkit.entity.Player;

public class PickBlock extends Serverbound {

	@Override
	public void onReceive(Player player) {
		new CreativePickBlockEvent(player).callEvent();
	}
}
