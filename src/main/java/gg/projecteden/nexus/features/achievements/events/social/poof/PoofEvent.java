package gg.projecteden.nexus.features.achievements.events.social.poof;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class PoofEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private final Player initiator, acceptor;

	public PoofEvent(Player initiator, Player acceptor) {
		this.initiator = initiator;
		this.acceptor = acceptor;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
