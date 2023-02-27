package gg.projecteden.nexus.features.commands.creative;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Permission(Group.MODERATOR)
public class MobCapCommand extends CustomCommand {

	public MobCapCommand(CommandEvent event) {
		super(event);
	}

	@Path("<amount>")
	@Description("Change the mob cap of a creative plot.")
	void run(@Arg("50") int amount) {
		runCommand("plot flag set mob-cap " + amount);
		runCommand("plot flag set hostile-cap " + amount);
		runCommand("plot flag set animal-cap " + amount);
		send("&3Set the mob cap to " + amount);
	}

}
