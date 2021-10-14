package gg.projecteden.nexus.features.events.mobevents.annotations;

import gg.projecteden.nexus.features.events.mobevents.types.common.MobEventType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Type {
	MobEventType value();
}
