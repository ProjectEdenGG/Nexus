package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;

public class SneakCommand extends CustomCommand {

	public SneakCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	@Description("Crouch")
	void run() {
		player().setSneaking(true);
	}

}
