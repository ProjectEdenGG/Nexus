package gg.projecteden.nexus.features.survival.models.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface NPCConfig {
	int npcId();

	String skinId();
}
