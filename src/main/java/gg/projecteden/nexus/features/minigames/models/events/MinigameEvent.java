package gg.projecteden.nexus.features.minigames.models.events;

import gg.projecteden.nexus.features.minigames.models.Arena;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;

@RequiredArgsConstructor
public abstract class MinigameEvent extends Event {
	@NonNull
	protected Arena arena;
}
