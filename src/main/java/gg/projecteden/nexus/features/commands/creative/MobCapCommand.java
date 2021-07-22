package gg.projecteden.nexus.features.commands.creative;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Permission("group.moderator")
public class MobCapCommand extends CustomCommand {

	public MobCapCommand(CommandEvent event) {
		super(event);
	}

	@Path("<amount>")
	void run(@Arg("50") int amount) {
		runCommand("plot set mob-cap " + amount);
		runCommand("plot set hostile-cap " + amount);
		runCommand("plot set animal-cap " + amount);
		send("&3Set the mob cap to " + amount);
	}

}
