package gg.projecteden.nexus.features.events.mobevents.annotations;

import gg.projecteden.nexus.features.events.mobevents.types.common.WorldSet.Dimension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Affects {
	Dimension[] value();
}
