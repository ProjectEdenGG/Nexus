package me.pugabyte.nexus.features.commands.creative;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

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
