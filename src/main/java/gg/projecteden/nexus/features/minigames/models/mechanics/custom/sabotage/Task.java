package gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.sabotage.MinigamerCompleteTaskPartEvent;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.taskpartdata.SegmentedTaskData;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.taskpartdata.TaskPartData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lexikiq.HasUniqueId;
import net.md_5.bungee.api.ChatColor;

@Getter
@RequiredArgsConstructor
public class Task {
    private final Tasks task;
    private int completed = 0;
    private TaskPartData data = null;

    /**
     * Must be called after running {@link #nextPart()}
     */
    public <T extends TaskPartData> T getData() {
        return (T) data;
    }

    public int getTaskSize() {
        return task.getParts().length;
    }

    public TaskPart nextPart() {
        if (completed >= task.getParts().length)
            return null;
        TaskPart part = task.getParts()[completed];
        if (data == null)
            data = part.createTaskPartData();
        return part;
    }

    public void partCompleted(Minigamer minigamer) {
        new MinigamerCompleteTaskPartEvent(minigamer, nextPart()).callEvent();
        completed += 1;
    }

    public void partCompleted(HasUniqueId player) {
        partCompleted(Minigamer.of(player));
    }

    public double progress() {
        nextPart();
        if (data instanceof SegmentedTaskData segmentedData)
            return segmentedData.progress();

        return (double) completed / getTaskSize();
    }

    public String progressStr() {
        nextPart();
        if (data instanceof SegmentedTaskData segmentedData)
            return segmentedData.progressStr();

        int length = getTaskSize();
        if (length == 1)
            return "";
        return completed + "/" + length;
    }

    public State getState() {
        if (getTaskSize() == completed)
            return State.COMPLETE;
        else if (completed > 0)
            return State.IN_PROGRESS;
        return State.UNSTARTED;
    }

    public String render() {
        TaskPart part = nextPart();
        if (part == null)
            part = task.getParts()[completed-1];
        String output = part.getName();
        String progress = progressStr();
        if (!progress.isEmpty())
            output += " (" + progress + ")";
        return getState().getChatColor() + output;
    }

    @Getter
    @RequiredArgsConstructor
    public enum State {
        COMPLETE(ChatColor.GREEN),
        IN_PROGRESS(ChatColor.YELLOW),
        UNSTARTED(ChatColor.WHITE);

        private final ChatColor chatColor;
    }
}
