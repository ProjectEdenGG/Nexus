package me.pugabyte.nexus.features.minigames.models.sabotage.taskpartdata;

import me.pugabyte.nexus.features.minigames.models.sabotage.TaskPart;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TaskPartDataFor {
    TaskPart[] value();
}
