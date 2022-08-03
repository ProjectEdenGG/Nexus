package gg.projecteden.nexus.features.survival;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ForDifficulty {
	Difficulty[] value();
}
