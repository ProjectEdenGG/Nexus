package gg.projecteden.nexus.features.achievements.events.travel;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class WarpEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private final Player player;

	public WarpEvent(Player player) {
		this.player = player;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
