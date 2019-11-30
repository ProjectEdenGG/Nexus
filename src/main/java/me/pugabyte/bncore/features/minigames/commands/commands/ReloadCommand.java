package me.pugabyte.bncore.features.minigames.commands.commands;

import me.pugabyte.bncore.features.minigames.commands.models.MinigamesCommand;
import me.pugabyte.bncore.features.minigames.commands.models.MinigamesCommandEvent;
import me.pugabyte.bncore.features.minigames.commands.models.MinigamesTabEvent;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;

import java.util.List;

public class ReloadCommand extends MinigamesCommand {

	public ReloadCommand() {
		this.name = "reload";
		this.permission = "manage";
		this.playerOnly = false;
	}

	@Override
	protected void execute(MinigamesCommandEvent event) {
		long startTime = System.currentTimeMillis();

		if (args.length == 1) {
			ArenaManager.read(args[0]);
		} else {
			ArenaManager.read();
		}

		event.reply("Reload time took " + (System.currentTimeMillis() - startTime) + "ms");
	}

	@Override
	protected List<String> tab(MinigamesTabEvent event) {
		if (args.length == 1)
			return ArenaManager.getNames(args[0]);

		return null;
	}

}
