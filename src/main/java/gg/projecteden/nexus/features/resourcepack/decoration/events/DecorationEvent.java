package gg.projecteden.nexus.features.resourcepack.decoration.events;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class DecorationEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList handlerList = new HandlerList();
	protected boolean cancelled = false;
	protected final Decoration decoration;

	public DecorationEvent(Player player, Decoration decoration) {
		super(player);
		this.decoration = decoration;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}
}
