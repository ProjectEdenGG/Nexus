package gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common;

import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.common.IChallengeProgress;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProgressClass {

	Class<? extends IChallengeProgress<?>> value();

}
