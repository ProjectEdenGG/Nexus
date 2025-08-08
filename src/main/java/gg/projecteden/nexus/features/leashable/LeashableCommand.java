package gg.projecteden.nexus.features.leashable;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Permission(Group.ADMIN)
public class LeashableCommand extends CustomCommand {

	public LeashableCommand(CommandEvent event) {
		super(event);
	}

	@Path("debug [enabled]")
	@Description("Toggle debug mode")
	void debug(Boolean enabled) {
		if (enabled == null)
			enabled = !Leashable.getDebuggers().contains(uuid());

		if (enabled)
			Leashable.getDebuggers().add(uuid());
		else
			Leashable.getDebuggers().remove(uuid());

		send(PREFIX + "Debug " + (enabled ? "&aEnabled" : "&cDisabled"));
	}
}
