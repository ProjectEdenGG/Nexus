package gg.projecteden.nexus.features.minigames.models.events.matches.minigamers;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MinigamerRespawnEvent extends MinigamerMatchEvent {

	public MinigamerRespawnEvent(Minigamer minigamer) {
		super(minigamer);
	}

	@Getter
	private static final HandlerList handlerList = new HandlerList();


	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}

}
