package me.pugabyte.nexus.features.minigames.models.sabotage;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TaskPartDataFor {
    TaskPart[] value();
}
