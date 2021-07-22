package gg.projecteden.nexus.features.store.perks.joinquit;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class VanishEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();

	public VanishEvent(Player player) {
		super(player);
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	@NotNull
	public HandlerList getHandlers() {
		return handlers;
	}

}
