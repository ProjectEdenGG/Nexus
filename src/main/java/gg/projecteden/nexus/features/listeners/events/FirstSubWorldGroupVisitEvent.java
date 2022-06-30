package gg.projecteden.nexus.features.listeners.events;

import gg.projecteden.nexus.utils.worldgroup.SubWorldGroup;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@Getter
public class FirstSubWorldGroupVisitEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();
	private final SubWorldGroup subWorldGroup;

	public FirstSubWorldGroupVisitEvent(@NotNull Player player, SubWorldGroup subWorldGroup) {
		super(player);
		this.subWorldGroup = subWorldGroup;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
