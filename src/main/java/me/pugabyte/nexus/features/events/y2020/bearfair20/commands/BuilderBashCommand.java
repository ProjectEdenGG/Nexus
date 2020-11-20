package me.pugabyte.nexus.features.events.y2020.bearfair20.commands;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

public class BuilderBashCommand extends CustomCommand {

	public BuilderBashCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommand("warp builderbash");
	}

}
