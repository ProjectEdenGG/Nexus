package me.pugabyte.nexus.features.minigolf;

import me.pugabyte.nexus.features.minigolf.models.MiniGolfUser;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

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
