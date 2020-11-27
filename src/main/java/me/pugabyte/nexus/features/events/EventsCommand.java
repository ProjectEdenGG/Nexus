package me.pugabyte.nexus.features.events;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.eventuser.EventUser;

public class EventsCommand extends CustomCommand {

	public EventsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("debug [player]")
	void debug(@Arg("self") EventUser eventUser) {
		send(toPrettyString(eventUser));
	}

}
