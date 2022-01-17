package gg.projecteden.nexus.framework.commands.models.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// TODO Support quotations with regex "(?:[^"\\]|\\.)*"

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Switch {
	char shorthand() default '-';

}
