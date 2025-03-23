package gg.projecteden.nexus.features.minigames.models.annotations;

import gg.projecteden.nexus.features.minigames.models.MatchStatistics;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MatchStatisticsClass {
	Class<? extends MatchStatistics> value();
}
