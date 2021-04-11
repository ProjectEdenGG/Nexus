package me.pugabyte.nexus.features.events.y2021.bearfair21.commands;

import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolf;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.PlayerUtils;

@Permission("group.staff")
public class MiniGolfCommand extends CustomCommand {

	public MiniGolfCommand(CommandEvent event) {
		super(event);
	}

	@Path("kit")
	void kit() {
		PlayerUtils.giveItems(player(), MiniGolf.getKit());
	}


}
