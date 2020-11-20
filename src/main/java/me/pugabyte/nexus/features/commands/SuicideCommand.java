package me.pugabyte.nexus.features.commands;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.GameMode;

public class SuicideCommand extends CustomCommand {

	public SuicideCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void suicide() {
		if (!WorldGroup.get(player()).equals(WorldGroup.SURVIVAL))
			error("You can only do this command in the survival world.");
		runCommand("god off");
		player().setGameMode(GameMode.SURVIVAL);
		player().setHealth(0.0);
	}


}
