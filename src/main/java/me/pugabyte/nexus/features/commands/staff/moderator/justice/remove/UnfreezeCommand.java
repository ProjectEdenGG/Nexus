package me.pugabyte.nexus.features.commands.staff.moderator.justice.remove;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.freeze.Freeze;
import me.pugabyte.nexus.utils.StringUtils;

import java.util.List;

@Permission("group.moderator")
public class UnfreezeCommand extends CustomCommand {

	public UnfreezeCommand(CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("Freeze");
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
