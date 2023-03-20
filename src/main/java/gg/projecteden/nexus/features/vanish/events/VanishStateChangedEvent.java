package gg.projecteden.nexus.features.vanish.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class VanishStateChangedEvent extends AbstractVanishEvent {

	public VanishStateChangedEvent(@NotNull Player who) {
		super(who);
	}

	// <editor-fold defaultstate="collapsed" desc="Boilerplate">
	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	// </editor-fold>

}
