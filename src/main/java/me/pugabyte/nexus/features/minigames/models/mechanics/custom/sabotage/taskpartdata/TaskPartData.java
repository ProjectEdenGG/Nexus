package me.pugabyte.nexus.features.minigames.models.mechanics.custom.sabotage.taskpartdata;

import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.sabotage.TaskPart;

/**
 * Custom data storage for tasks (like MatchData)
 */
@RequiredArgsConstructor
public class TaskPartData {
    protected final TaskPart task;

    // TODO: javadocs

    public boolean hasRunnable() {
        return false;
    }

    public void runnable(Match match) {}
}
