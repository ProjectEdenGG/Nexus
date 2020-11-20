package me.pugabyte.nexus.features.store.perks.joinquit;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class VanishEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	@Getter
	private Player player;

	public VanishEvent(Player player) {
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
