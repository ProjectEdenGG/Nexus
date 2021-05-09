package me.pugabyte.nexus.features.commands.staff;

import eden.utils.TimeUtils.Time;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Cooldown;
import me.pugabyte.nexus.framework.commands.models.annotations.Cooldown.Part;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

@Permission("group.staff")
@Aliases({"gpi", "griefinfo"})
public class GriefProtectionInfoCommand extends CustomCommand {

	public GriefProtectionInfoCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Cooldown(global = true, value = @Part(value = Time.SECOND, x = 30))
	void info() {
		String message = "Grief is not allowed, and staff will repair any grief you find.";
		runCommand("ch qm g " + message);
	}

}

