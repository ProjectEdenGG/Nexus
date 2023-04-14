package gg.projecteden.nexus.features.chat.commands;

import gg.projecteden.nexus.features.chat.Koda;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Vararg;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import lombok.NonNull;

@Permission(Group.SENIOR_STAFF)
public class KodaCommand extends CustomCommand {

	public KodaCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Make Koda say something")
	void say(@Vararg String message) {
		Koda.say(message);
	}

	@Description("Reload Koda's configuration from disk")
	void reload() {
		Koda.reloadConfig();
		send(PREFIX + Koda.getTriggers().size() + " responses loaded from disk");
	}

}
