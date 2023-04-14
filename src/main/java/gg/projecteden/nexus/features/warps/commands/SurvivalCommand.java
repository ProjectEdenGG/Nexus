package gg.projecteden.nexus.features.warps.commands;

import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import lombok.NonNull;

public class SurvivalCommand extends CustomCommand {

	public SurvivalCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Teleport to the survival world spawn")
	void warp() {
		runCommand("warp survival");
	}

}
