package me.pugabyte.bncore.features.minigames.commands;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Cooldown;
import me.pugabyte.bncore.framework.commands.models.annotations.Cooldown.Part;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Time;

@Cooldown(@Part(Time.WEEK))
public class ParkourRewardCommand extends CustomCommand {

	public ParkourRewardCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void reward() {
		runCommandAsConsole("eco give " + player().getName() + " 40");
	}

}
