package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Cooldown;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Permission(Group.STAFF)
@Aliases({"gpi", "griefinfo"})
public class GriefProtectionInfoCommand extends CustomCommand {

	public GriefProtectionInfoCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Cooldown(global = true, value = TickTime.SECOND, x = 30)
	@Description("Send a predefined message explaining that grief is not allowed")
	void info() {
		runCommand("ch qm g Grief is not allowed, and staff will repair any grief you find.");
	}

}

