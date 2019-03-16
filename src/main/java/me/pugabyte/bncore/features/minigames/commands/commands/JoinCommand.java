package me.pugabyte.bncore.features.minigames.commands.commands;

import me.pugabyte.bncore.features.minigames.commands.MinigamesCommand;
import me.pugabyte.bncore.features.minigames.commands.MinigamesCommandEvent;
import me.pugabyte.bncore.features.minigames.commands.MinigamesTabEvent;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.models.exceptions.InvalidInputException;

import java.util.List;

public class JoinCommand extends MinigamesCommand {

	public JoinCommand() {
		this.name = "join";
		this.permission = "use";
	}

	@Override
	protected void execute(MinigamesCommandEvent event) throws InvalidInputException {
		if (args.length == 0)
			throw new InvalidInputException("You must supply an arena name to join");

		minigamer.join(args[0]);
	}

	@Override
	protected List<String> tab(MinigamesTabEvent event) {
		if (args.length != 1)
			return null;

		return ArenaManager.getNames(args[0]);
	}

}
