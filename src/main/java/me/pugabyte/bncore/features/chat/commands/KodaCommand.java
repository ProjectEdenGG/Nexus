package me.pugabyte.bncore.features.chat.commands;

import lombok.NonNull;
import me.pugabyte.bncore.features.chat.Koda;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Permission("group.seniorstaff")
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
