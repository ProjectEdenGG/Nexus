package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class StatusCommand extends CustomCommand {

	public StatusCommand(CommandEvent event) {
		super(event);
	}

	String perm = "featherboard.group.";
	String userPerm = perm + "user";

	@Path
	void toggle() {
		if (!perms()) {
			if (player().hasPermission(userPerm)) removeUserPerm();
			else addUserPerm();
		}
		runCommand("fb off");
	}

	@Path("(on|enable|true)")
	void on() {
		if (!perms()) addUserPerm();
		else runCommand("fb on");
	}

	@Path("(off|disable|false)")
	void off() {
		if (!perms()) removeUserPerm();
		else runCommand("fb off");
	}

	boolean perms() {
		if (player().hasPermission(perm + "moderator")) return true;
		if (player().hasPermission(perm + "operator")) return true;
		return player().hasPermission(perm + "admin");
	}

	public void addUserPerm() {
		runConsoleCommand("pex user " + player().getName() + " add " + userPerm);
	}

	public void removeUserPerm() {
		runConsoleCommand("pex user " + player().getName() + " remove " + userPerm);
	}

}
