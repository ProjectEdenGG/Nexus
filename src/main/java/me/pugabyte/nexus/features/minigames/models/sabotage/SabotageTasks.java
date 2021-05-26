package me.pugabyte.nexus.features.minigames.models.sabotage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public enum SabotageTasks {
    REACTOR(true, TaskPart.REACTOR)
    ;

    private final boolean isSabotage;
    /**
     * Item that players right click to open the task
     */
    private final TaskPart[] parts;

    SabotageTasks(boolean isSabotage, TaskPart... parts) {
        this.isSabotage = isSabotage;
        this.parts = parts;
    }
}
