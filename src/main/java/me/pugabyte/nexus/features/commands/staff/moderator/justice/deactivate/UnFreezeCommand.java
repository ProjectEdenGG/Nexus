package me.pugabyte.nexus.features.commands.staff.moderator.justice.deactivate;

import me.pugabyte.nexus.features.commands.staff.moderator.justice.misc._JusticeCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.freeze.Freeze;

import java.util.List;

@Permission("group.moderator")
public class UnFreezeCommand extends _JusticeCommand {

	public UnFreezeCommand(CommandEvent event) {
		super(event);
	}

	@Path("<players...>")
	void unfreeze(@Arg(type = Freeze.class) List<Freeze> players) {
		for (Freeze freeze : players)
			try {
				freeze.deactivate(uuid());
			} catch (Exception ex) {
				event.handleException(ex);
			}
	}

}
