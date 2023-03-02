package gg.projecteden.nexus.features.commands.info;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

public class TicketPreFilledCommand extends CustomCommand {

	public TicketPreFilledCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Description("View a few different options on sending pre-filled tickets to Staff.")
	void run() {
		line();
		send(json()
				.next(" &3|&3| ").group()
				.next("&eI've been griefed ").command("/ticket I've been griefed").hover("&eClick here if you've been griefed and", "&ewould like staff assistance to fix it").group()
				.next(" &3|&3| ").group()
				.next("&eLava placement ").command("/ticket I need lava placed").hover("&eClick here if you'd like lava placement").group()
				.next(" &3|&3| ").group()
				.next("&eHarassment").command("/ticket I'm being harassed").hover("&eClick here if you're being", "&eharassed by another player").group()
				.next(" &3|&3| ")
		);
		line();
	}

}
