package gg.projecteden.nexus.features.events;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface EdenEventGameConfig {
	String prefix();

	String world();

	String playRegion();
}
