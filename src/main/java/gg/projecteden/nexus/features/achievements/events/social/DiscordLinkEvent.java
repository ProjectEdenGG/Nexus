package gg.projecteden.nexus.features.achievements.events.social;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class DiscordLinkEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private final Player player;

	public DiscordLinkEvent(Player player) {
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
