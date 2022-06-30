package gg.projecteden.nexus.features.listeners.events;

import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@Getter
public class WorldGroupChangedEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();
	private final WorldGroup oldWorldGroup, newWorldGroup;

	public WorldGroupChangedEvent(@NotNull Player player, WorldGroup oldWorldGroup, WorldGroup newWorldGroup) {
		super(player);
		this.player = player;
		this.oldWorldGroup = oldWorldGroup;
		this.newWorldGroup = newWorldGroup;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
