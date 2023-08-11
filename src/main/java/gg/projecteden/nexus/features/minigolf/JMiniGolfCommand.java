package gg.projecteden.nexus.features.minigolf;

import gg.projecteden.nexus.features.minigolf.models.GolfBallColor;
import gg.projecteden.nexus.features.minigolf.models.MiniGolfUser;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@HideFromWiki // TODO
@Permission(Group.ADMIN)
public class JMiniGolfCommand extends CustomCommand {
	MiniGolfUser user;

	public JMiniGolfCommand(CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent()) {
			user = MiniGolfUtils.getUser(uuid());
			if (user == null) {
				user = new MiniGolfUser(uuid(), GolfBallColor.WHITE);
				user.sendMessage("new user");
			} else {
				user.sendMessage("get user");
			}

		}
	}

	@Path("join")
	void join() {
		if (MiniGolf.isPlaying(user)) {
			send("already playing minigolf");
			return;
		}

		MiniGolf.join(user);
		send("started playing minigolf");
	}

	@Path("quit")
	void quit() {
		if (!MiniGolf.isPlaying(user)) {
			send("not playing minigolf");
			return;
		}

		MiniGolf.quit(user);
		send("stopped playing minigolf");
	}

	@Path("kit")
	void kit() {
		if (!MiniGolf.isPlaying(user)) {
			send("not playing minigolf");
			return;
		}

		user.giveKit();
		send("giving kit");
	}

	@Path("setColor <color>")
	void setColor(GolfBallColor color) {
		if (!MiniGolf.isPlaying(user)) {
			send("not playing minigolf");
			return;
		}

		user.setGolfBallColor(color);
	}

	@Path("debug <boolean>")
	void debug(boolean bool) {
		user.setDebug(bool);
		send("set debug to: " + user.isDebug());
	}

}
