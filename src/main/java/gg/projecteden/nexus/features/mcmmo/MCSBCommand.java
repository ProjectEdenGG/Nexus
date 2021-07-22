package gg.projecteden.nexus.features.mcmmo;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Redirect(from = "/mcsbk", to = "/mcsb k")
@Redirect(from = "/mcsbc", to = "/mcsb c")
public class MCSBCommand extends CustomCommand {

	public MCSBCommand(CommandEvent event) {
		super(event);
	}

	@Path("(k|keep)")
	void keep() {
		runCommand("mcscoreboard keep");
	}

	@Path("(c|clear|remove)")
	void remove() {
		runCommand("mcscoreboard clear");
	}

	@Path
	void run() {
		send("&c/mcsb <k[eep]|c[lear]>");
	}

}
