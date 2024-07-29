package gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.sabotage;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerMatchEvent;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.TaskPart;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class MinigamerCompleteTaskPartEvent extends MinigamerMatchEvent {
	private final @NotNull TaskPart taskPart;
	public MinigamerCompleteTaskPartEvent(@NotNull final Minigamer minigamer, @NotNull final TaskPart taskPart) {
		super(minigamer);
		this.taskPart = taskPart;
	}

	private static final HandlerList handlerList = new HandlerList();

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}
}
