package gg.projecteden.nexus.features.minigames.models.events.arenas;

import gg.projecteden.nexus.features.minigames.models.events.MinigameEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class AllArenasLoadedEvent extends MinigameEvent {

	@Getter
	private static final HandlerList handlerList = new HandlerList();

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}

}
