package gg.projecteden.nexus.features.chat.commands;

import gg.projecteden.nexus.features.chat.Koda;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;

@Permission(Group.SENIOR_STAFF)
public class KodaCommand extends CustomCommand {

	public KodaCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<message...>")
	void say(String message) {
		Koda.say(message);
	}

	@Path("reload")
	void reload() {
		Koda.reloadConfig();
		send(PREFIX + Koda.getTriggers().size() + " responses loaded from disk");
	}

}
