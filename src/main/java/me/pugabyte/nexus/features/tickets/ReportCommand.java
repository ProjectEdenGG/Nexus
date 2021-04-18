package me.pugabyte.nexus.features.tickets;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

public class ReportCommand extends CustomCommand {

	public ReportCommand(@NonNull CommandEvent event) {
		super(event);
	}

	// TODO Ticket builder for reporting a player

}
