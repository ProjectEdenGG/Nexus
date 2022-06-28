package gg.projecteden.nexus.features.listeners.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called before a player changes worlds
 */
@Getter
public class PlayerChangingWorldsEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private final World fromWorld, toWorld;
	@Setter
	private boolean cancelled;

	public PlayerChangingWorldsEvent(@NotNull Player player, World fromWorld, World toWorld) {
		super(player);
		this.player = player;
		this.fromWorld = fromWorld;
		this.toWorld = toWorld;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
