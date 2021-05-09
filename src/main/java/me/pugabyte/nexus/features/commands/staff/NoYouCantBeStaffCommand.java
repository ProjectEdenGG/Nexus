package me.pugabyte.nexus.features.commands.staff;

import eden.utils.TimeUtils.Time;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Cooldown;
import me.pugabyte.nexus.framework.commands.models.annotations.Cooldown.Part;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

@Aliases("nycbs")
@Permission("group.staff")
public class NoYouCantBeStaffCommand extends CustomCommand {

	public NoYouCantBeStaffCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Cooldown(global = true, value = @Part(value = Time.SECOND, x = 30))
	void nycbs() {
		runCommand("ch qm g One of the most basic duties of staff members is to help players. How do you expect to do that if you know *absolutely nothing* about the server?");
	}

}
