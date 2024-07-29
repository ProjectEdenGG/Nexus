package gg.projecteden.nexus.features.vanish.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PreVanishToggleEvent extends AbstractVanishEvent implements Cancellable {
	@Getter
	@Setter
	private boolean cancelled;

	public PreVanishToggleEvent(@NotNull Player who) {
		super(who);
	}

	// <editor-fold defaultstate="collapsed" desc="Boilerplate">
	@Getter
	private static final HandlerList handlerList = new HandlerList();

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}
	// </editor-fold>

}
