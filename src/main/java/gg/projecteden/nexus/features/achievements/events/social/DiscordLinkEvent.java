package gg.projecteden.nexus.features.achievements.events.social;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class DiscordLinkEvent extends Event {
	private static final HandlerList handlerList = new HandlerList();
	private final Player player;

	public DiscordLinkEvent(Player player) {
		this.player = player;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}

}
