package gg.projecteden.nexus.features.listeners.events;

import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@Getter
public class FirstWorldGroupVisitEvent extends PlayerEvent {
	private static final HandlerList handlerList = new HandlerList();
	private final WorldGroup worldGroup;

	public FirstWorldGroupVisitEvent(@NotNull Player player, WorldGroup worldGroup) {
		super(player);
		this.worldGroup = worldGroup;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}

}
