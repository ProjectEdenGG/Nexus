package gg.projecteden.nexus.features.achievements.events.social.poof;

import org.bukkit.entity.Player;

public class PoofHereEvent extends PoofEvent {

	public PoofHereEvent(Player initiator, Player acceptor) {
		super(initiator, acceptor);
	}

}
