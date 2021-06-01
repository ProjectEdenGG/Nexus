package me.pugabyte.nexus.features.minigames.models.sabotage;

import lombok.Getter;
import org.apache.commons.lang.Validate;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum Tasks {
    SWIPE_CARD(TaskType.COMMON, TaskPart.SWIPE_CARD),
    LIGHTS(TaskType.SABOTAGE, TaskPart.LIGHTS),
    REACTOR(TaskType.SABOTAGE, TaskPart.REACTOR)
    ;

    private final TaskType taskType;
    /**
     * Item that players right click to open the task
     */
    private final TaskPart[] parts;

    Tasks(TaskType taskType, TaskPart... parts) {
        Validate.notEmpty(parts, "Tasks must specify atleast one part");
        this.taskType = taskType;
        this.parts = parts;
    }

    public enum TaskType {
        COMMON,
        SHORT,
        LONG,
        SABOTAGE;
    }

    public static Set<Tasks> getByType(TaskType type) {
        return Arrays.stream(values()).filter(tasks -> tasks.getTaskType() == type).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static Set<Tasks> crewmateTasks() {
        return Arrays.stream(values()).filter(tasks -> tasks.getTaskType() != TaskType.SABOTAGE).collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
