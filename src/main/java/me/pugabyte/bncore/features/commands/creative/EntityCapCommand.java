package me.pugabyte.bncore.features.commands.creative;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Permission("group.moderator")
public class EntityCapCommand extends CustomCommand {

	public EntityCapCommand(CommandEvent event) {
		super(event);
	}

	@Path("<amount>")
	void run(@Arg("50") int amount) {
		runCommand("plot set entity-cap " + amount);
		send("&3Set the entity cap to " + amount);
	}

}
