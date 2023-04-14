package gg.projecteden.nexus.framework.commandsv2.annotations.path;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Hides a subcommand from <i><code>/&lt;parent&gt; help</code></i>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HideFromHelp {
}
