package me.pugabyte.bncore.features.minigames.commands.commands;

import me.pugabyte.bncore.features.minigames.commands.models.MinigamesCommand;
import me.pugabyte.bncore.features.minigames.commands.models.MinigamesCommandEvent;

public class HelpCommand extends MinigamesCommand {

	public HelpCommand() {
		this.name = "help";
		this.permission = "use";
		this.playerOnly = false;
	}

	@Override
	protected void execute(MinigamesCommandEvent event) {
		event.reply("Help menu");
	}

}
