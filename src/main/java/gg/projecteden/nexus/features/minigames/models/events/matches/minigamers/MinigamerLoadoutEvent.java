package gg.projecteden.nexus.features.minigames.models.events.matches.minigamers;

import gg.projecteden.nexus.features.minigames.models.Loadout;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class MinigamerLoadoutEvent extends MinigamerMatchEvent {
	private final @NotNull Loadout loadout;

	public MinigamerLoadoutEvent(@NotNull Minigamer minigamer, @NotNull Loadout loadout) {
		super(minigamer);
		this.loadout = loadout;
	}

	private static final HandlerList handlerList = new HandlerList();


	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}
}
