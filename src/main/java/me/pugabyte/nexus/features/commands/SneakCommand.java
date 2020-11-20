package me.pugabyte.nexus.features.commands;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

public class SneakCommand extends CustomCommand {

	public SneakCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		player().setSneaking(true);
	}

}
