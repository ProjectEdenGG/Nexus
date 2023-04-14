package gg.projecteden.nexus.features.justice.deactivate;

import gg.projecteden.nexus.features.justice.misc._JusticeCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.freeze.Freeze;

import java.util.List;

@Permission(Group.MODERATOR)
public class UnFreezeCommand extends _JusticeCommand {

	public UnFreezeCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Path("<players...>")
	@Description("Unfreeze a player")
	void unfreeze(@ErasureType(Freeze.class) List<Freeze> players) {
		for (Freeze freeze : players)
			try {
				freeze.deactivate(uuid());
			} catch (Exception ex) {
				event.handleException(ex);
			}
	}

}
