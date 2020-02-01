package me.pugabyte.bncore.features.commands.aliases;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class MCSKCommand extends CustomCommand {

	public MCSKCommand(CommandEvent event) {
		super(event);
	}

	@Path("(k|keep)")
	void keep() {
		runCommand("mcscoreboard keep");
	}

	@Path("(c|clear|remove)")
	void remove(){
		runCommand("mscoreboard clear");
	}

	@Path
	void run(){
		send("&c/mcsb <k[eep]|c[lear]>");
	}

}
