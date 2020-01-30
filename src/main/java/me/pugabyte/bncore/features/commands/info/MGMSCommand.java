package me.pugabyte.bncore.features.commands.info;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class MGMSCommand extends CustomCommand {

	public MGMSCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void help() {
		line();
		send("&3Join us on &eSaturdays&3, &e4&3-&e6 &ePM &eEST &3for &eMinigame Nights&3!");
		line();
		send("&3We have a variety of minigames including &eInfection&3, &eCapture the Flag&3, &ePaintball&3, &eFree for All&3, and many more.");
		line();
		send("&3Use &c/gl &3to get the &egame lobby&3.");
	}

}
