package gg.projecteden.nexus.features.minigames.commands;

import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import lombok.NonNull;

@Aliases("gl")
public class GamelobbyCommand extends CustomCommand {

	public GamelobbyCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Teleport to the minigame lobby")
	void teleport() {
		runCommand("warp minigames");
	}

}
