package me.pugabyte.bncore.features.minigames.commands.commands;

import me.pugabyte.bncore.features.minigames.commands.models.MinigamesCommand;
import me.pugabyte.bncore.features.minigames.commands.models.MinigamesCommandEvent;
import me.pugabyte.bncore.models.exceptions.InvalidInputException;

public class QuitCommand extends MinigamesCommand {

	public QuitCommand() {
		this.name = "quit";
		this.permission = "use";
	}

	@Override
	protected void execute(MinigamesCommandEvent event) throws InvalidInputException {
		if (minigamer.getMatch() == null)
			throw new InvalidInputException("You are not in a match");

		minigamer.quit();
	}

}
