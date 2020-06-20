package me.pugabyte.bncore.features.commands;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Permission("essentials.beezooka")
public class BZCommand extends CustomCommand {

	public BZCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommand("beezooka");
	}
}
