package me.pugabyte.bncore.features.commands.info;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class TicketPreFilledCommand extends CustomCommand {

	public TicketPreFilledCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		line();
		json(" &3|&3| || &eI've been griefed ||cmd:/ticket I've been griefed||ttp:&eClick here if you've been griefed and \n&ewould like staff assistance to fix it." +
				"|| &3|&3| || &eLava placement ||cmd:/ticket I need lava placed||ttp:&eClick here if you'd like lava placement." +
				"|| &3|&3| || &eHarassment ||cmd:/ticket I'm being harassed.||ttp:&eClick here if you're being \n&eharassed by another player.");
		line();
	}

}
