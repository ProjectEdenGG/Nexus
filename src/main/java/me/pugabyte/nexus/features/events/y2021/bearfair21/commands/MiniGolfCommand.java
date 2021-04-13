package me.pugabyte.nexus.features.events.y2021.bearfair21.commands;

import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolf;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolfUser;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.ColorType;
import me.pugabyte.nexus.utils.StringUtils;

@Permission("group.staff")
public class MiniGolfCommand extends CustomCommand {
	MiniGolfUser user;

	public MiniGolfCommand(CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = MiniGolf.getUser(uuid());
	}

	@Path("kit")
	void getKit() {
		MiniGolf.giveKit(player());
	}

	@Path("play")
	void play() {
		if (user != null)
			error("already playing");

		MiniGolf.getUsers().add(new MiniGolfUser(uuid()));
		getKit();
		send("playing minigolf");
		send("Users: " + MiniGolf.getUsers().toString());
	}

	@Path("quit")
	void quit() {
		if (user == null)
			error("not playing");

		MiniGolf.getUsers().remove(user);
		MiniGolf.takeKit(player());
		send("quit minigolf");
		send("Users: " + MiniGolf.getUsers().toString());
	}

	@Path("color <colorType>")
	void color(ColorType colorType) {
		if (colorType == null)
			error("Unknown color");

		user.setColor(colorType);
		send("set color to: " + StringUtils.camelCase(user.getColor()));
	}


}
