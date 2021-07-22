package gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.taskpartdata;

import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.TaskPart;
import lombok.Getter;

@Getter
public abstract class SegmentedTaskData extends TaskPartData {
    private int segment = 0;
    private final int segments;
    public SegmentedTaskData(TaskPart task, int segments) {
        super(task);
        this.segments = segments;
    }
    public void increment() {
        segment += 1;
    }
    public double progress() {
        return (double) segment / segments;
    }
    public String progressStr() {
        return segment + "/" + segments;
    }
}
