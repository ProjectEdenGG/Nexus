package gg.projecteden.nexus.features.warps.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;

public class SurvivalCommand extends CustomCommand {

	public SurvivalCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void warp() {
		runCommand("rtp");
	}

}
