package me.pugabyte.bncore.features.minigames.commands.commands;

import me.pugabyte.bncore.features.minigames.commands.models.MinigamesCommand;
import me.pugabyte.bncore.features.minigames.commands.models.MinigamesCommandEvent;
import me.pugabyte.bncore.features.minigames.commands.models.MinigamesTabEvent;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.managers.MatchManager;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.models.exceptions.InvalidInputException;

import java.util.List;
import java.util.Optional;

public class StartCommand extends MinigamesCommand {

	public StartCommand() {
		this.name = "start";
		this.permission = "manage";
	}

	@Override
	protected void execute(MinigamesCommandEvent event) throws InvalidInputException {
		if (args.length == 0) {
			if (minigamer != null) {
				if (minigamer.getMatch() != null) {
					minigamer.getMatch().start();
				} else {
					throw new InvalidInputException("You are not in a match");
				}
			} else {
				throw new InvalidInputException("You must supply an arena name");
			}
		} else {
			Optional<Arena> optionalArena = ArenaManager.get(args[0]);
			if (optionalArena.isPresent()) {
				Optional<Match> optionalMatch = MatchManager.get(optionalArena.get());

				if (!optionalMatch.isPresent()) {
					throw new InvalidInputException("There is no match running for that arena");
				}

				optionalMatch.get().start();
			} else {
				throw new InvalidInputException("That arena doesn't exist");
			}
		}
	}

	@Override
	protected List<String> tab(MinigamesTabEvent event) {
		if (args.length == 1)
			return ArenaManager.getNames(args[0]);

		return null;
	}

}

