package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.nameplates.Nameplates;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.push.PushService;
import gg.projecteden.nexus.models.push.PushUser;
import gg.projecteden.nexus.utils.LuckPermsUtils.PermissionChange;
import gg.projecteden.nexus.utils.Tasks;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PushCommand extends CustomCommand {

	public PushCommand(CommandEvent event) {
		super(event);
	}

	public static @NotNull CompletableFuture<Boolean> set(UUID uuid, Boolean allowPush) {
		return CompletableFuture.supplyAsync(() -> {
			PushService service = Nameplates.get().getPushService();
			PushUser user = service.get(uuid);
			final boolean enabled = allowPush != null ? allowPush : !user.isEnabled();
			user.setEnabled(enabled);
			service.save(user);
			return enabled;
		}, Tasks::async);
	}

	@Path("[enable]")
	void toggle(Boolean enable) {
		set(uuid(), enable).thenAccept(enabled -> send("&ePushing will be turned " + (enabled ? "&aon" : "&coff") + "&e shortly."));
	}

}
