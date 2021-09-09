package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.LuckPermsUtils.PermissionChange;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PushCommand extends CustomCommand {
	public static final String PERMISSION = "stoppushing.allow";

	public PushCommand(CommandEvent event) {
		super(event);
	}

	public static @NotNull CompletableFuture<Void> set(UUID uuid, boolean enabled) {
		return PermissionChange.set().uuid(uuid).permission(PERMISSION).value(enabled).runAsync();
	}

	@Path("[enable]")
	void toggle(Boolean enable) {
		if (enable == null)
			enable = !player().hasPermission(PERMISSION);

		if (enable)
			set(uuid(), true).thenRun(() -> send("&ePushing will be turned &aon&e shortly."));
		else
			set(uuid(), false).thenRun(() -> send("&ePushing will be turned &coff&e shortly."));
	}

}
