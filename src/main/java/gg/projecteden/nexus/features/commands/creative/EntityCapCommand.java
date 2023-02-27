package gg.projecteden.nexus.features.commands.creative;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Permission(Group.MODERATOR)
public class EntityCapCommand extends CustomCommand {

	public EntityCapCommand(CommandEvent event) {
		super(event);
	}

	@Path("<amount>")
	@Description("Change the entity cap of a creative plot.")
	void run(@Arg("50") int amount) {
		runCommand("plot flag set entity-cap " + amount);
		send("&3Set the entity cap to " + amount);
	}

}
