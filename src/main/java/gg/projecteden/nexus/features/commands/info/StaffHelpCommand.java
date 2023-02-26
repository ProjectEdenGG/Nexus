package gg.projecteden.nexus.features.commands.info;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Description("Sends a few commands in chat relating to how to get help from staff.")
public class StaffHelpCommand extends CustomCommand {

	public StaffHelpCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Override
	public void help() {
		line();
		send("&eStaff Help Commands");
		line();
		send(json("&3[+] &c/ticket <problem>").suggest("/ticket").hover("&eClick here to submit a ticket"));
		send(json("&3[+] &c/staff").suggest("/staff").hover("&eShow a list of all current staff"));
		send(json("&3[+] &c/onlinestaff").suggest("/onlinestaff").hover("&eShow a list of online staff members"));
	}

}
