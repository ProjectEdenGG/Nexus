package me.pugabyte.bncore.features.radio;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

// TODO on release: Remove admin perms, rename command to RadioCommand

@Permission("group.admin")
public class BNRadioCommand extends CustomCommand {
	public BNRadioCommand(CommandEvent event) {
		super(event);
	}

	@Path("join")
	void joinRadio() {
		send("TODO");
	}

	@Path("leave")
	void leaveRadio() {
		send("TODO");
	}

	@Path("toggle")
	void toggleRadio() {
		send("TODO");
	}

	@Path("song")
	void songInfo() {
		send("TODO");
	}

	@Path("playlist")
	void playlist() {
		send("TODO");
	}

	@Path("players")
	@Permission("group.staff")
	void listListeners() {
		send("TODO");
	}

	@Path("reload")
	@Permission("group.admin")
	void reloadConfig() {
		send("TODO");
	}
}
