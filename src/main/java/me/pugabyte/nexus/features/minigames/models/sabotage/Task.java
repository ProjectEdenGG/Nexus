package me.pugabyte.nexus.features.minigames.models.sabotage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.minigames.mechanics.Sabotage;
import me.pugabyte.nexus.features.minigames.models.sabotage.taskpartdata.SegmentedTaskData;
import me.pugabyte.nexus.features.minigames.models.sabotage.taskpartdata.TaskPartData;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

@Getter
@RequiredArgsConstructor
public class Task {
    private final Tasks task;
    private int completed = 0;
    /**
     * Instantiated when calling {@link #nextPart()}
     */
    @Getter
    private @MonotonicNonNull TaskPartData data = null;

    public int getTaskSize() {
        return task.getParts().length;
    }

    public TaskPart nextPart() {
        if (completed == task.getParts().length)
            return null;
        TaskPart part = task.getParts()[completed];
        if (data == null)
            data = Sabotage.createTaskPartDataFor(part);
        return part;
    }

    public void partCompleted() {
        completed += 1;
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
}
