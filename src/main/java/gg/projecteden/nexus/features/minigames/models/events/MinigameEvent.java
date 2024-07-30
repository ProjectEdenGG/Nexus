package gg.projecteden.nexus.features.minigames.models.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public abstract class MinigameEvent extends Event {
	@Getter
	protected static final HandlerList handlerList = new HandlerList();

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}
}
