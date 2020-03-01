package me.pugabyte.bncore.features.minigames.models.annotations;

import me.pugabyte.bncore.features.minigames.models.scoreboards.MinigameScoreboard.Type;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Scoreboard {
	Type sidebarType() default Type.MATCH;
	boolean teams() default true;
}
