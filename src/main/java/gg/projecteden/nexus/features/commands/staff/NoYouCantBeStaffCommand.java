package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Cooldown;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.utils.TimeUtils.TickTime;

@Aliases("nycbs")
@Permission("group.staff")
public class NoYouCantBeStaffCommand extends CustomCommand {

	public NoYouCantBeStaffCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Cooldown(global = true, value = TickTime.SECOND, x = 30)
	void nycbs() {
		runCommand("ch qm g One of the most basic duties of staff members is to help players. How do you expect to do that if you know *absolutely nothing* about the server?");
	}

}
