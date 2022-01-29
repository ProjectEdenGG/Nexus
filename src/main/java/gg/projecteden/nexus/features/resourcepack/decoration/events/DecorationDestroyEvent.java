package gg.projecteden.nexus.features.resourcepack.decoration.events;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import org.bukkit.entity.Player;

public class DecorationDestroyEvent extends DecorationEvent {

	public DecorationDestroyEvent(Player player, Decoration decoration) {
		super(player, decoration);
	}

}
