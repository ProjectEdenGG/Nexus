package me.pugabyte.nexus.features.minigames.models.sabotage.taskpartdata;

import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.sabotage.TaskPart;

/**
 * Custom data storage for tasks (like MatchData)
 */
@RequiredArgsConstructor
public class TaskPartData {
    private final TaskPart task;

    public boolean hasRunnable() {
        return false;
    }

    public void runnable(Match match) {}
}
