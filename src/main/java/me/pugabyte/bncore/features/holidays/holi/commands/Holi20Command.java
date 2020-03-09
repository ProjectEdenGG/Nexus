package me.pugabyte.bncore.features.holidays.holi.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class Holi20Command extends CustomCommand {

	public Holi20Command(CommandEvent event) {
		super(event);
	}

	@Path
	void holi() {
		send(PREFIX + "Coming soon!");
	}

}
