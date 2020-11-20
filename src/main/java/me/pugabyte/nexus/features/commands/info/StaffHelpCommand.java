package me.pugabyte.nexus.features.commands.info;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

@Aliases("staffhelpcommands")
public class StaffHelpCommand extends CustomCommand {

	public StaffHelpCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void help() {
		line();
		send("&eStaff Help Commands");
		line();
		send(json("&3[+] &c/ticket <problem>").suggest("/ticket").hover("&eClick here to submit a ticket"));
		send(json("&3[+] &c/staff").suggest("/staff").hover("&eShow a list of all current staff"));
		send(json("&3[+] &c/onlinestaff").suggest("/onlinestaff").hover("&eShow a list of online staff members"));
	}

}
