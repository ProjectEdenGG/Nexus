package gg.projecteden.nexus.features.minigames.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;

@Aliases("gl")
public class GamelobbyCommand extends CustomCommand {

	public GamelobbyCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	@Description("Teleport to the minigame lobby")
	void teleport() {
		runCommand("warp minigames");
	}

}
