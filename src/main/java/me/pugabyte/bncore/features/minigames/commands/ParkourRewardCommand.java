package me.pugabyte.bncore.features.minigames.commands;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Cooldown;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Cooldown(7 * 24 * 60 * 60 * 20)
public class ParkourRewardCommand extends CustomCommand {

	public ParkourRewardCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void reward() {
		runConsoleCommand("eco give " + player().getName() + " 40");
	}

}
