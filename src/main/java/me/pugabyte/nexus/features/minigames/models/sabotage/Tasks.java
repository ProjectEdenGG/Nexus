package me.pugabyte.nexus.features.minigames.models.sabotage;

import lombok.Getter;

@Getter
public enum Tasks {
    REACTOR(TaskType.COMMON, TaskPart.SWIPE_CARD)
    ;

    private final TaskType taskType;
    /**
     * Item that players right click to open the task
     */
    private final TaskPart[] parts;

    Tasks(TaskType taskType, TaskPart... parts) {
        this.taskType = taskType;
        this.parts = parts;
    }

    public enum TaskType {
        COMMON,
        SHORT,
        LONG,
        SABOTAGE;
    }
}
