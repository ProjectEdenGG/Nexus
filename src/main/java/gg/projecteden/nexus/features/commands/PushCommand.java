package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.nexus.features.nameplates.Nameplates;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;

public class PushCommand extends CustomCommand {

	public PushCommand(CommandEvent event) {
		super(event);
	}

	@Async
	@NoLiterals
	@Path("[enable]")
	@Description("Toggle entity collision")
	void toggle(Boolean enabled) {
		Nameplates.get().getPushService().edit(uuid(), user -> {
			user.setEnabled(enabled != null ? enabled : !user.isEnabled());
			send(PREFIX + (user.isEnabled() ? "&aEnabled" : "&cDisabled"));
		});
	}

}
