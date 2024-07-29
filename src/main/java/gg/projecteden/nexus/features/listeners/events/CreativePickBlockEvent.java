package gg.projecteden.nexus.features.listeners.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class CreativePickBlockEvent extends PlayerEvent {
	@Getter
	private static final HandlerList handlerList = new HandlerList();

	public CreativePickBlockEvent(@NotNull Player who) {
		super(who);
	}

	@NotNull
	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}
}
