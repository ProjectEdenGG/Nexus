package me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.sabotage;

import lombok.Getter;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.MinigamerEvent;
import me.pugabyte.nexus.features.minigames.models.sabotage.TaskPart;
import org.jetbrains.annotations.NotNull;

@Getter
public class MinigamerCompleteTaskPartEvent extends MinigamerEvent {
	private final @NotNull TaskPart taskPart;
	public MinigamerCompleteTaskPartEvent(@NotNull final Minigamer minigamer, @NotNull final TaskPart taskPart) {
		super(minigamer);
		this.taskPart = taskPart;
	}
}
