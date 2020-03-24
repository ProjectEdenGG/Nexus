package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.CooldownException;
import me.pugabyte.bncore.models.cooldown.CooldownService;
import me.pugabyte.bncore.utils.Time;

@Aliases("nycbs")
@Permission("group.staff")
public class NoYouCantBeStaffCommand extends CustomCommand {

	public NoYouCantBeStaffCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void nycbs() {
		try {
			new CooldownService().check("staff", "nycbs", Time.SECOND.x(30));
			runCommand("ch qm g One of the most basic duties of staff members is to help players. How do you expect to do that if you know *absolutely nothing* about the server?");
		} catch (CooldownException ex) {
		}
	}

}
