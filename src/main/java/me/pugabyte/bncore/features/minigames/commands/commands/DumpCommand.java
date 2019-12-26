package me.pugabyte.bncore.features.minigames.commands.commands;

import me.pugabyte.bncore.Utils;
import me.pugabyte.bncore.features.minigames.commands.models.MinigamesCommand;
import me.pugabyte.bncore.features.minigames.commands.models.MinigamesCommandEvent;
import me.pugabyte.bncore.features.minigames.commands.models.MinigamesTabEvent;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;

import java.util.List;
import java.util.Optional;

public class DumpCommand extends MinigamesCommand {

	public DumpCommand() {
		this.name = "dump";
		this.permission = "manage";
	}

	@Override
	protected void execute(MinigamesCommandEvent event) throws InvalidInputException {
		if (args.length == 0)
			throw new InvalidInputException("You must supply an arena name");

		Optional<Arena> optionalArena = ArenaManager.get(args[0]);
		if (!optionalArena.isPresent())
			throw new InvalidInputException("Arena not found");

		Utils.dump(optionalArena.get());
	}

	@Override
	protected List<String> tab(MinigamesTabEvent event) {
		if (args.length == 1)
			return ArenaManager.getNames(args[0]);

		return null;
	}

}
