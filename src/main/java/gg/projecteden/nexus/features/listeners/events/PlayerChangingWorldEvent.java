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
public class PlayerChangingWorldEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList handlerList = new HandlerList();
	private final World fromWorld, toWorld;
	@Setter
	private boolean cancelled;

	public PlayerChangingWorldEvent(@NotNull Player player, World fromWorld, World toWorld) {
		super(player);
		this.player = player;
		this.fromWorld = fromWorld;
		this.toWorld = toWorld;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}

}
