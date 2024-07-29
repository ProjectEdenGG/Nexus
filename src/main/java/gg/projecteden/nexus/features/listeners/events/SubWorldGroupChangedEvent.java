package gg.projecteden.nexus.features.listeners.events;

import gg.projecteden.nexus.utils.worldgroup.SubWorldGroup;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@Getter
public class SubWorldGroupChangedEvent extends PlayerEvent {
	@Getter
	private static final HandlerList handlerList = new HandlerList();
	private final SubWorldGroup oldSubWorldGroup, newSubWorldGroup;

	public SubWorldGroupChangedEvent(@NotNull Player player, SubWorldGroup oldSubWorldGroup, SubWorldGroup newSubWorldGroup) {
		super(player);
		this.player = player;
		this.oldSubWorldGroup = oldSubWorldGroup;
		this.newSubWorldGroup = newSubWorldGroup;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}

}
