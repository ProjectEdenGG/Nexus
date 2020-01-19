package me.pugabyte.bncore.features.commands.ranks;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class GuestCommand extends CustomCommand {

	public GuestCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void guest() {
		line(5);
		send("&7Guest &3is the rank &eeveryone &3starts out as. Guests have all &ebasic &3permissions, nothing special.");
		line();
		RanksCommand.ranksReturn(player());
	}
}
