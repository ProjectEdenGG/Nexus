package gg.projecteden.nexus.features.commands;

import gg.projecteden.annotations.Async;
import gg.projecteden.nexus.features.nameplates.Nameplates;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

public class PushCommand extends CustomCommand {

	public PushCommand(CommandEvent event) {
		super(event);
	}

	@Async
	@Path("[enable]")
	void toggle(Boolean enabled) {
		Nameplates.get().getPushService().edit(uuid(), user -> {
			user.setEnabled(enabled != null ? enabled : !user.isEnabled());
			send(PREFIX + (user.isEnabled() ? "&aEnabled" : "&cDisabled"));
		});
	}

}
