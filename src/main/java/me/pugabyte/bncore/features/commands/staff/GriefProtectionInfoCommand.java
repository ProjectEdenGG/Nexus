package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.CooldownException;
import me.pugabyte.bncore.models.cooldown.CooldownService;

@Permission("group.staff")
@Aliases({"gpi", "griefinfo"})
public class GriefProtectionInfoCommand extends CustomCommand {

	public GriefProtectionInfoCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void info() {
		try {
			new CooldownService().check("staff", "grief", 30 * 20);

			String message = "Grief is not allowed, and staff will repair any grief you find. However, we do have protection stones (/pstoneinfo) for prevention.";
			runCommand("ch qm g " + message);
		} catch (CooldownException ex) {
			send("Prevented double sending");
		}
	}

}

