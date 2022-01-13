package gg.projecteden.nexus.features.listeners.events;

import gg.projecteden.nexus.utils.WorldGroup;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class FirstWorldGroupVisitEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();
	private final Player player;
	@Getter
	private final WorldGroup worldGroup;

	public FirstWorldGroupVisitEvent(@NotNull Player player, WorldGroup worldGroup) {
		super(player);
		this.player = player;
		this.worldGroup = worldGroup;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
