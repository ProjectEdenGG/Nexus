package gg.projecteden.nexus.framework.commands.models.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Makes a command run synchronously
 * <p>
 * This is the default for all commands. This is currently only useful in {@link gg.projecteden.nexus.features.commands.staff.admin.AccountTransferCommand.Transferable AccountTransferCommand.Transferable}
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Sync {
}