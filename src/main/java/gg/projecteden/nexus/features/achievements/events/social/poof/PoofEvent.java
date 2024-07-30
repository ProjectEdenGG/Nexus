package gg.projecteden.nexus.features.achievements.events.social.poof;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class PoofEvent extends Event {
	@Getter
	private static final HandlerList handlerList = new HandlerList();
	private final Player initiator, acceptor;

	public PoofEvent(Player initiator, Player acceptor) {
		this.initiator = initiator;
		this.acceptor = acceptor;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}

}
