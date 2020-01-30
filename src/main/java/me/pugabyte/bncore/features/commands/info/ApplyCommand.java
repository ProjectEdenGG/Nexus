package me.pugabyte.bncore.features.commands.info;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class ApplyCommand extends CustomCommand {

	public ApplyCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void help() {
		line();
		send("&3Ready to apply for &b&oModerator&3?");
		send("&3How does your name look in blue, &b&o" + player().getName() + "&3? :)");
		send("&3If you think you are ready for this position, you can fill out an application here:");
		json("&ehttps://bnn.gg/apply/mod||url:https://bnn.gg/apply/mod");
	}

}
