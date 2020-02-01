package me.pugabyte.bncore.features.commands.info;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

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
		send(json2("&3[+] &c/ticket <problem>").suggest("/ticket").hover("&eClick here to submit a ticket"));
		send(json2("&3[+] &c/staff").suggest("/staff").hover("&eShow a list of all current staff"));
		send(json2("&3[+] &c/onlinestaff").suggest("/onlinestaff").hover("&eShow a list of online staff members"));
	}

}
