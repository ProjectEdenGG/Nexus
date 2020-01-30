package me.pugabyte.bncore.features.commands.aliases;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class IveBeenGriefedCommand extends CustomCommand {

	public IveBeenGriefedCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		send("&cOh no! &eYou were griefed? &3Well, not to worry! Griefing is against the rules, " +
				"and a staff member will happily fix the grief for you using our special roll back plugin. " +
				"All you have to do is stand at the &elocation &3of the grief and type &c/ticket <message>&3. " +
				"Please be &edescriptive &3when making a ticket, as it helps staff do their job more quickly and accurately.");
	}

}
