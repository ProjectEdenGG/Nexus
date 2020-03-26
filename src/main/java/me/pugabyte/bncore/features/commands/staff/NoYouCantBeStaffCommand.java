package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Cooldown;
import me.pugabyte.bncore.framework.commands.models.annotations.Cooldown.Part;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Time;

@Aliases("nycbs")
@Permission("group.staff")
public class NoYouCantBeStaffCommand extends CustomCommand {

	public NoYouCantBeStaffCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Cooldown(id = "staff", value = @Part(value = Time.SECOND, x = 30))
	void nycbs() {
		runCommand("ch qm g One of the most basic duties of staff members is to help players. How do you expect to do that if you know *absolutely nothing* about the server?");
	}

}
