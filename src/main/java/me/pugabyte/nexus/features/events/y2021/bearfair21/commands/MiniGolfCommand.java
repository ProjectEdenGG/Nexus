package me.pugabyte.nexus.features.events.y2021.bearfair21.commands;

import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolf;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolfColor;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Confirm;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.bearfair21.MiniGolf21User;
import me.pugabyte.nexus.models.bearfair21.MiniGolf21UserService;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.Particle;

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
		MiniGolf.giveKit(user);
	}

	@Path("play")
	void play() {
		if (user.isPlaying())
			error("You are already playing");

		user.setPlaying(true);
		service.save(user);

		player().setCollidable(false);

		MiniGolf.takeKit(user);
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

		player().setCollidable(true);

		MiniGolf.takeKit(user);
		send(PREFIX + "You have quit playing");
	}

	@Path("color <color>")
	void color(MiniGolfColor color) {
		if (color == null)
			error("Unknown color");

		if (user.getSnowball() == null)
			MiniGolf.takeKit(user);

		user.setMiniGolfColor(color);
		service.save(user);

		if (user.getSnowball() == null)
			MiniGolf.giveKit(user);

		MiniGolfColor _color = user.getMiniGolfColor();
		String message = PREFIX + "Set color to: ";
		String colorName = StringUtils.camelCase(_color);

		if (_color.equals(MiniGolfColor.RAINBOW))
			send(message + StringUtils.Rainbow.apply(colorName));
		else
			send(message + _color.getColorType().getChatColor() + colorName);
	}

	@Path("particle <particle>")
	void particle(Particle particle) {
		if (particle == null)
			error("Unknown particle");

		user.setParticle(particle);
		service.save(user);

		send(PREFIX + "Set particle to: " + StringUtils.camelCase(user.getParticle()));
	}

	@Path("clearDatabase")
	@Confirm
	void resetData() {
		service.clearCache();
		service.deleteAll();
		service.clearCache();
	}


}
