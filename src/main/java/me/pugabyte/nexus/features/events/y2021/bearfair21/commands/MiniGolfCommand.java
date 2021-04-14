package me.pugabyte.nexus.features.events.y2021.bearfair21.commands;

import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolf;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.bearfair21.MiniGolf21User;
import me.pugabyte.nexus.models.bearfair21.MiniGolf21UserService;
import me.pugabyte.nexus.utils.ColorType;
import me.pugabyte.nexus.utils.StringUtils;

@Permission("group.staff")
public class MiniGolfCommand extends CustomCommand {
	private MiniGolf21User user;
	private final MiniGolf21UserService service = new MiniGolf21UserService();

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
		if (user.isPlaying())
			error("You are already playing");

		user.setPlaying(true);
		service.save(user);

		MiniGolf.takeKit(player());
		getKit();
		send(PREFIX + "You are now playing");
	}

	@Path("quit")
	void quit() {
		if (!user.isPlaying())
			error("You are not playing");

		user.setPlaying(false);
		service.save(user);

		if (user.getSnowball() != null) {
			user.getSnowball().remove();
			user.setSnowball(null);
		}

		MiniGolf.takeKit(player());
		send(PREFIX + "You have quit playing");
	}

	@Path("color <colorType>")
	void color(ColorType colorType) {
		if (colorType == null)
			error("Unknown color");

		user.setColor(colorType);
		service.save(user);

		ColorType color = user.getColor();
		send(PREFIX + "Set color to: " + StringUtils.colorize(color.getChatColor() + StringUtils.camelCase(color)));
	}


}
