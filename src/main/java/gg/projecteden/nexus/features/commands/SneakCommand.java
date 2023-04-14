package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import lombok.NonNull;

public class SneakCommand extends CustomCommand {

	public SneakCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Crouch")
	void run() {
		player().setSneaking(true);
	}

}
