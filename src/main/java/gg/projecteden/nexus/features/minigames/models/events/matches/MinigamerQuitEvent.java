package gg.projecteden.nexus.features.minigames.models.events.matches;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerEvent;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MinigamerQuitEvent extends MinigamerEvent {

	public MinigamerQuitEvent(@NotNull Minigamer minigamer) {
		super(minigamer);
	}

	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
