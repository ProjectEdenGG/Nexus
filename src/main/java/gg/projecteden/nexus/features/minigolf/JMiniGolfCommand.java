package gg.projecteden.nexus.features.minigolf;

import gg.projecteden.nexus.features.minigolf.models.MiniGolfUser;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Permission("group.admin")
public class JMiniGolfCommand extends CustomCommand {
	MiniGolfUser user;

	public JMiniGolfCommand(CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent()) {
			user = MiniGolfUtils.getUser(uuid());
			if (user == null) {
				user = new MiniGolfUser(uuid());
				user.sendMessage("new user");
			} else {
				user.sendMessage("get user");
			}

		}
	}

	@Path("join")
	void join() {
		MiniGolf.join(user);
		send("started playing minigolf");
	}

	@Path("quit")
	void quit() {
		MiniGolf.quit(user);
		send("stopped playing minigolf");
	}

	@Path("debug <boolean>")
	void debug(boolean bool) {
		user.setDebug(bool);
		send("set debug to: " + user.isDebug());
	}


}
