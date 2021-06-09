package me.pugabyte.nexus.features.minigames.models.events;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.minigames.models.Arena;
import org.bukkit.event.Event;

@RequiredArgsConstructor
public abstract class MinigameEvent extends Event {
	@NonNull
	protected Arena arena;
}
