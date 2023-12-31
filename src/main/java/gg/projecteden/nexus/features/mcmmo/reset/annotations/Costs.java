package gg.projecteden.nexus.features.mcmmo.reset.annotations;

import gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUser.SkillTokenType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Costs {
	Cost[] value() default {};

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@Repeatable(value = Costs.class)
	@interface Cost {
		SkillTokenType token();
		int value();
	}
}



