package gg.projecteden.nexus.features.commands.aliases;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

public class IveBeenGriefedCommand extends CustomCommand {

	public IveBeenGriefedCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Description("View information on what to do when you get griefed.")
	void run() {
		send("&cOh no! &eYou were griefed? &3Well, not to worry! Griefing is against the rules, " +
				"and a staff member will happily fix the grief for you using our special roll back plugin. " +
				"All you have to do is stand at the &elocation &3of the grief and type &c/ticket <message>&3. " +
				"Please be &edescriptive &3when making a ticket, as it helps staff do their job more quickly and accurately.");
	}

}
