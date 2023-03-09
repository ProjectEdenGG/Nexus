package gg.projecteden.nexus.features.justice.deactivate;

import gg.projecteden.nexus.features.justice.misc._JusticeCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.freeze.Freeze;

import java.util.List;

@Permission(Group.MODERATOR)
public class UnFreezeCommand extends _JusticeCommand {

	public UnFreezeCommand(CommandEvent event) {
		super(event);
	}

	@Path("<players...>")
	@Description("Unfreeze a player")
	void unfreeze(@Arg(type = Freeze.class) List<Freeze> players) {
		for (Freeze freeze : players)
			try {
				freeze.deactivate(uuid());
			} catch (Exception ex) {
				event.handleException(ex);
			}
	}

}
