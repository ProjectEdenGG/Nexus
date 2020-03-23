package me.pugabyte.bncore.features.votes.vps;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class JVPSCommand extends CustomCommand {

	public JVPSCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
	}

}
