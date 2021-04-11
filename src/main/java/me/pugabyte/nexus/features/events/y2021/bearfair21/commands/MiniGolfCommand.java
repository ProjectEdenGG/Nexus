package me.pugabyte.nexus.features.events.y2021.bearfair21.commands;

import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolf;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolfUser;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

@Permission("group.staff")
public class MiniGolfCommand extends CustomCommand {

	public MiniGolfCommand(CommandEvent event) {
		super(event);
	}

	@Path("kit")
	void getKit() {
		MiniGolf.giveKit(player());
	}

	@Path("play")
	void play() {
		if (MiniGolf.getUser(uuid()) != null)
			error("already playing");

		MiniGolf.getUsers().add(new MiniGolfUser(uuid()));
		getKit();
		send("playing minigolf");
	}

	@Path("quit")
	void quit() {
		MiniGolfUser user = MiniGolf.getUser(uuid());
		if (user == null)
			error("not playing");

		MiniGolf.getUsers().remove(user);
		MiniGolf.takeKit(player());
		send("quit minigolf");
	}


}
