package gg.projecteden.nexus.features.events.mobevents.annotations;

import gg.projecteden.nexus.features.events.mobevents.types.common.IMobEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Instance {
	Class<? extends IMobEvent> value();

}
