package me.pugabyte.nexus.features.commands;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

public class SignUpCommand extends CustomCommand {

	public SignUpCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void signup() {
		send("&4Note: &cMember rank is no longer gained by signing up on our website. Check /member for more info");
		send("&ehttps://bnn.gg/signup");
	}
}
