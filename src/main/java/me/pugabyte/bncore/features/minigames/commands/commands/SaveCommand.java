package me.pugabyte.bncore.features.minigames.commands.commands;

import me.pugabyte.bncore.features.minigames.commands.models.MinigamesCommand;
import me.pugabyte.bncore.features.minigames.commands.models.MinigamesCommandEvent;
import me.pugabyte.bncore.features.minigames.commands.models.MinigamesTabEvent;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.models.Arena;

import java.util.List;

public class SaveCommand extends MinigamesCommand {

	public SaveCommand() {
		this.name = "save";
		this.permission = "manage";
		this.playerOnly = false;
	}

	@Override
	protected void execute(MinigamesCommandEvent event) {
		long startTime = System.currentTimeMillis();

		if (args.length == 1) {
			Arena.write(args[0]);
		} else {
			Arena.write();
		}

		event.reply("Save time took " + (System.currentTimeMillis() - startTime) + "ms");
	}

	@Override
	protected List<String> tab(MinigamesTabEvent event) {
		if (args.length == 1)
			return ArenaManager.getNames(args[0]);

		return null;
	}

}
