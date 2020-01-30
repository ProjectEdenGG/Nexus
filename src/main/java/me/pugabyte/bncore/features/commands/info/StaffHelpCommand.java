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
		json("&3[+] &c/ticket <problem>||sgt:/ticket||ttp:&eClick here to submit a ticket.");
		json("&3[+] &c/staff||sgt:/staff||ttp:&eShow a list of all current staff.");
		json("&3[+] &c/onlinestaff||sgt:/onlinestaff||ttp:&eShow a list of online staff members.");
	}

}
