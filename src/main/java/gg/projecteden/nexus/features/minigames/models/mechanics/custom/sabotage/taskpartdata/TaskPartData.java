package gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.taskpartdata;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.TaskPart;
import lombok.RequiredArgsConstructor;

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
