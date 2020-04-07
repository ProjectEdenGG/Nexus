package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Cooldown;
import me.pugabyte.bncore.framework.commands.models.annotations.Cooldown.Part;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Time;

@Permission("group.staff")
@Aliases({"gpi", "griefinfo"})
public class GriefProtectionInfoCommand extends CustomCommand {

	public GriefProtectionInfoCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Cooldown(id = "staff", value = @Part(value = Time.SECOND, x = 30))
	void info() {
		String message = "Grief is not allowed, and staff will repair any grief you find.";
		runCommand("ch qm g " + message);
	}

}

